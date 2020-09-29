package example.testapp.na.core.managers.notifications

import android.app.Notification
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.support.v4.app.NotificationCompat
import android.view.View
import android.widget.RemoteViews
import example.testapp.na.R
import example.testapp.na.core.managers.app.ScheduleHelper
import example.testapp.na.core.managers.app.SchedulingUpdatesManager
import example.testapp.na.core.managers.support.SharedCalendar
import example.testapp.na.core.receivers.DelayReceiver
import example.testapp.na.core.receivers.DismissReceiver
import example.testapp.na.data.entities.notes.Note
import example.testapp.na.data.entities.notes.NotesRepo
import example.testapp.na.data.entities.notes.notescontent.ContentImage
import example.testapp.na.data.preferences.PreferencesHelper
import example.testapp.na.screens.main.view.MainActivity
import example.testapp.na.tools.credentials.Extras
import example.testapp.na.tools.credentials.Messages
import example.testapp.na.tools.credentials.Types
import kotlinx.coroutines.experimental.Deferred
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.async
import kotlinx.coroutines.experimental.delay
import rc.extensions.scope.RCScope
import rc.extensions.streaming.Subscription
import rc.extensions.workers.Workers
import javax.inject.Inject
import kotlin.reflect.KClass

class NotificationManager @Inject internal constructor(
        private val contentManager: NotificationContentHelper,
        private val notesRepo: NotesRepo,
        private val schedulerHelper: ScheduleHelper,
        private val preferencesHelper: PreferencesHelper) : NotificationHelper, RCScope {

    override fun buildNotification(context: Context, intent: Intent): Deferred<Notification> = async(UI) {

        val notesLayout = RemoteViews(context.packageName, intent.getIntExtra(Extras.EXTRA_LAYOUT_TYPE, R.layout.notification_note))

        when (intent.getIntExtra(Extras.EXTRA_LAYOUT_TYPE, R.layout.notification_note)) {
            R.layout.notification_note -> fillNoteLayout(notesLayout, intent)
            R.layout.notification_image -> fillImageLayout(notesLayout, intent)
            R.layout.notification_list -> fillListLayout(notesLayout, intent)
        }

        contentManager.setUpContentLayout(context, notesLayout,
                R.id.noteContentNotification, intent.getIntegerArrayListExtra(Extras.EXTRA_NOTE_CONTENT))

        setUpMainViews(notesLayout, intent)

        setListeners(notesLayout, intent, context)

        updateState(intent)

        delay(1000)

        return@async NotificationCompat.Builder(context, Extras.ACTION_NOTIFICATION)
                .setAutoCancel(true)
                .setContentIntent(PendingIntent.getActivity(context, intent.getLongExtra(Extras.EXTRA_NOTE_ID, 0).toInt(),
                        Intent(context, MainActivity::class.java), PendingIntent.FLAG_UPDATE_CURRENT))
                .setCustomBigContentView(notesLayout)
                .setSmallIcon(R.drawable.add_note_belled_icon)
                .setDefaults(Notification.DEFAULT_SOUND).build()
    }

    override fun updateState(intent: Intent) {
        notesRepo.getNoteById(intent.getLongExtra(Extras.EXTRA_NOTE_ID, 0))
                .subscribe(object : Subscription<Note> {
                    override fun onComplete(result: Note) {
                        result.params!!.delayedMillis = 0L
                        if (!intent.getBooleanExtra(Extras.EXTRA_ONE_OFF, false)) {
                            schedulerHelper.schedule(result, false, {
                                notesRepo.updateNote(result).doOnComplete {
                                    trackNote(result)
                                    schedulerHelper.startOperation(result.id)
                                }.execute()
                            })
                        } else {
                            schedulerHelper.cancel(result, {
                                result.params!!.type = Types.NOTE_TYPE_NO_SIGNAL
                                notesRepo.updateNote(result).doOnComplete {
                                    trackNote(result)
                                }.execute()
                            })
                        }
                    }
                }, Workers.default()).execute()
    }

    private fun setListeners(layout: RemoteViews, intent: Intent, context: Context) {
        layout.setOnClickPendingIntent(R.id.btnDismissNotification,
                getPendingSelfIntent(context, Extras.EXTRA_TYPE_DISMISS, Extras.ACTION_DISMISS,
                        intent.getLongExtra(Extras.EXTRA_NOTE_ID, 0),
                        intent.getIntExtra(Extras.EXTRA_REMINDER_TYPE, Types.NOTE_TYPE_NOTIFICATION),
                        DismissReceiver::class))

        layout.setOnClickPendingIntent(R.id.btnSnoozeNotification,
                getPendingSelfIntent(context, Extras.EXTRA_TYPE_SNOOZE, Extras.ACTION_DELAY,
                        intent.getLongExtra(Extras.EXTRA_NOTE_ID, 0),
                        intent.getIntExtra(Extras.EXTRA_REMINDER_TYPE, Types.NOTE_TYPE_NOTIFICATION),
                        DelayReceiver::class))
    }

    private fun setUpMainViews(layout: RemoteViews, intent: Intent) {
        layout.setTextViewText(R.id.titleTextNotification, intent.getStringExtra(Extras.EXTRA_NOTE_TITLE))
        layout.setTextViewText(R.id.dateTextNotification,
                intent.getStringExtra(Extras.EXTRA_NOTE_DATE) + "  " + intent.getStringExtra(Extras.EXTRA_NOTE_TIME))
        layout.setTextViewText(R.id.btnSnoozeNotification, Messages.TEXT_DELAY + " " +
                (preferencesHelper.getNotificationDelay() / 60000L) + " " + Messages.TEXT_DELAY_2)
    }

    private fun fillNoteLayout(layout: RemoteViews, intent: Intent) {
        layout.setTextViewText(R.id.noteNotificationText, intent.getStringExtra(Extras.EXTRA_NOTE_TEXT))
        intent.getStringExtra(Extras.EXTRA_NOTE_TEXT).apply {
            if (this == null) {
                layout.setViewVisibility(R.id.noteNotificationTextParent, View.GONE)
            }
        }
    }

    private fun fillListLayout(layout: RemoteViews, intent: Intent) {
        val listItems = intent.getStringArrayListExtra(Extras.EXTRA_NOTE_LIST)
        arrayOf(R.id.noteNotificationListItem1,
                R.id.noteNotificationListItem2,
                R.id.noteNotificationListItem3).forEachIndexed { index, i ->
            try {
                layout.setTextViewText(i, listItems[index])
            } catch (ex: Exception) {
                layout.setViewVisibility(i, View.GONE)
            }
        }
    }

    override fun delay(intent: Intent, type: Int) {
        notesRepo.getNoteById(intent.getLongExtra(Extras.EXTRA_NOTE_ID, 0)).subscribe(object : Subscription<Note> {
            override fun onComplete(result: Note) {
                result.params?.apply {
                    delayedMillis = System.currentTimeMillis() + preferencesHelper.getNotificationDelay()
                    if (result.params!!.type == Types.NOTE_TYPE_NO_SIGNAL) {
                        result.params!!.type = type
                    }
                    SharedCalendar().apply {
                        setMillis(delayedMillis)
                        date = getStringDate()
                        time = getStringTime()
                        schedulerHelper.schedule(result, true, {
                            notesRepo.updateNote(result).doOnComplete {
                                trackNote(result)
                                schedulerHelper.startOperation(result.id)
                            }.execute()
                        })
                    }
                }
            }
        }, Workers.ui()).execute()
    }

    private fun trackNote(note: Note) {
        SchedulingUpdatesManager.addId(note.id)
    }

    override fun dismiss(intent: Intent) {
        notesRepo.getNoteById(intent.getLongExtra(Extras.EXTRA_NOTE_ID, 0))
                .subscribe(object : Subscription<Note> {
                    override fun onComplete(result: Note) {
                        schedulerHelper.cancel(result, {
                            notesRepo.updateNote(result).doOnComplete {
                                trackNote(result)
                            }.execute()
                        })
                    }
                }, Workers.ui()).execute()
    }

    private fun fillImageLayout(layout: RemoteViews, intent: Intent) {
        notesRepo.getNoteByIdOnUi(intent.getLongExtra(Extras.EXTRA_NOTE_ID, 0))
                .subscribe(object : Subscription<Note> {
                    override fun onComplete(result: Note) {
                        layout.setImageViewBitmap(R.id.noteNotificationImage,
                                (result.entities.first { it is ContentImage } as ContentImage).image)
                    }
                }, Workers.ui()).execute()
    }

    private fun <R : BroadcastReceiver> getPendingSelfIntent(context: Context, type: String, action: String,
                                                             id: Long, remType: Int, target: KClass<R>): PendingIntent {
        val intent = Intent(action)
        intent.setClass(context, target.java)
        intent.putExtra(type, true)
        intent.putExtra(Extras.EXTRA_NOTE_ID, id)
        intent.putExtra(Extras.EXTRA_REMINDER_TYPE, remType)
        return PendingIntent.getBroadcast(context, id.toInt(), intent, PendingIntent.FLAG_UPDATE_CURRENT)
    }
}