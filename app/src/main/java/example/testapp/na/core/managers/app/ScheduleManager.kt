package example.testapp.na.core.managers.app

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import example.testapp.na.R
import example.testapp.na.core.managers.support.ISharedCalendar
import example.testapp.na.core.managers.support.SharedCalendar
import example.testapp.na.core.receivers.AlarmsReceiver
import example.testapp.na.core.receivers.NotificationsReceiver
import example.testapp.na.data.entities.notes.Note
import example.testapp.na.data.entities.notes.notescontent.*
import example.testapp.na.data.preferences.PreferencesHelper
import example.testapp.na.tools.credentials.Extras
import example.testapp.na.tools.credentials.Types
import io.realm.RealmList
import kotlinx.coroutines.experimental.launch
import java.util.*
import javax.inject.Inject

class ScheduleManager @Inject constructor(
        private val context: Context,
        private val preferencesHelper: PreferencesHelper) : ScheduleHelper {

    private val sharedCalendar: ISharedCalendar = SharedCalendar()

    private val operations = arrayListOf<Pair<Long, () -> Unit>>()

    override fun schedule(note: Note, delayed: Boolean, onScheduled: () -> Unit) {
        if (note.params!!.timeMillis != 0L) {
            if (note.params!!.type == Types.NOTE_TYPE_NOTIFICATION) {
                scheduleNotification(note, delayed)
            } else if (note.params!!.type == Types.NOTE_TYPE_ALARM) {
                scheduleAlarm(note, delayed)
            }
        }
        onScheduled()
    }

    override fun startOperation(id: Long) {
        launch {
            operations.firstOrNull { it.first == id }?.let {
                it.second.invoke()
                println("NA: " + operations.size)
            }
            operations.removeAll { it.first == id }
            println("NA: " + operations.size)
        }
    }

    private fun scheduleNotification(note: Note, delayed: Boolean) {
        if (!note.archived) {
            val schedulable = noteIntent(Types.NOTE_TYPE_NOTIFICATION).apply {
                        setType(this, note)
                        setReminderType(this, note)
                        setContent(this, note)
                        setRepeating(this, note)
                        setOneOff(this, note)
                    }
            val millis = getRepeatingTime(note, if (delayed) note.params?.delayedMillis!! else note.params?.timeMillis!!)
            addTask(note, schedulable, millis, delayed)
        }
    }

    private fun addTask(note: Note, schedulable: Intent, millis: Long, delayed: Boolean) {
        note.params!!.apply {
            if (delayed) {
                delayedMillis = millis
            } else {
                timeMillis = millis
            }
            sharedCalendar.setMillis(if (!delayed) timeMillis else delayedMillis)
            time = sharedCalendar.getStringTime()
            date = sharedCalendar.getStringDate()
            sharedCalendar.setMillis(0L)
        }
        setDefaults(schedulable, note)
        val intent = PendingIntent.getBroadcast(context, note.id.toInt(), schedulable, PendingIntent.FLAG_UPDATE_CURRENT)
        println("NA: task added!")
        operations.add(Pair(note.id, {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT) {
                alarmManager().setExact(AlarmManager.RTC_WAKEUP, millis, intent)
            } else {
                alarmManager().set(AlarmManager.RTC_WAKEUP, millis, intent)
            }
        }))
    }

    override fun getRepeatingTime(note: Note, millis: Long): Long {
        return note.params!!.let {
            when {
                it.weekDays!!.isNotEmpty() -> it.weekDays!!.let { l ->
                    if ((millis > System.currentTimeMillis()) or (preferencesHelper.wasTurnedOff())) {
                        millis
                    } else {
                        l.sort()
                        with(Calendar.getInstance()) {
                            val picked = get(Calendar.DAY_OF_WEEK).let { d ->
                                it.weekDays?.firstOrNull { it > d }
                                        ?: it.weekDays?.firstOrNull { it <= d }
                            }
                            if (picked!! > get(Calendar.DAY_OF_WEEK)) {
                                set(Calendar.DAY_OF_WEEK, get(Calendar.DAY_OF_WEEK) + (picked - get(Calendar.DAY_OF_WEEK)))
                            } else if (picked <= get(Calendar.DAY_OF_WEEK)) {
                                set(Calendar.DAY_OF_WEEK, get(Calendar.DAY_OF_WEEK) + ((7 - get(Calendar.DAY_OF_WEEK)) + picked))
                                set(Calendar.WEEK_OF_MONTH, get(Calendar.WEEK_OF_MONTH) + 1)
                            }
                            timeInMillis
                        }
                    }
                }
                (it.repeatingInterval != 0L) and (it.repeatingPeriod != 0L) -> {
                    return if (preferencesHelper.wasTurnedOff()) {
                        millis
                    } else {
                        var after = millis
                        while (after <= System.currentTimeMillis()) {
                            after = if (after <= System.currentTimeMillis())
                                getNextMillisCustom(millis, it.repeatingInterval, it.repeatingPeriod) else after
                        }
                        after
                    }
                }
                else -> millis
            }
        }
    }

    private fun getNextMillisCustom(millis: Long, interval: Long, period: Long): Long {
        Calendar.getInstance().let {
            it.timeInMillis = millis
            when (period.toInt()) {
                1 -> it.set(Calendar.MINUTE, (it.get(Calendar.MINUTE) + interval.toInt()))
                2 -> it.set(Calendar.HOUR, (it.get(Calendar.HOUR) + interval.toInt()))
                3 -> it.set(Calendar.DAY_OF_MONTH, (it.get(Calendar.DAY_OF_MONTH) + interval.toInt()))
                4 -> it.set(Calendar.WEEK_OF_YEAR, (it.get(Calendar.WEEK_OF_YEAR) + interval.toInt()))
                5 -> it.set(Calendar.MONTH, (it.get(Calendar.MONTH) + interval.toInt()))
                6 -> it.set(Calendar.YEAR, (it.get(Calendar.YEAR) + interval.toInt()))
            }
            return it.timeInMillis
        }
    }

    override fun setOneOff(intent: Intent, note: Note) {
        note.params?.apply {
            if (weekDays!!.isEmpty() and (repeatingInterval == 0L) and (repeatingPeriod == 0L)) {
                intent.putExtra(Extras.EXTRA_ONE_OFF, true)
            }
        }
    }

    override fun setRepeating(intent: Intent, note: Note) {
        intent.apply {
            if (note.params!!.weekDays!!.isNotEmpty()) {
                putIntegerArrayListExtra(Extras.EXTRA_NOTE_WEEKDAYS, arrayListOf<Int>()
                        .apply { addAll(note.params!!.weekDays!!.apply { sort() }) })
            } else if ((note.params!!.repeatingInterval != 0L) and (note.params!!.repeatingPeriod != 0L)) {
                putExtra(Extras.EXTRA_NOTE_INTERVAL, note.params!!.repeatingInterval)
                putExtra(Extras.EXTRA_NOTE_REPEAT_PERIOD, note.params!!.repeatingPeriod)
            }
        }
    }

    override fun setContent(intent: Intent, note: Note) {
        intent.apply {
            arrayListOf<Int>().apply {
                if (note.entities.any { it is ContentList }) {
                    add(R.drawable.main_screen_note_list)
                }
                if (note.entities.any { it is ContentNote }) {
                    add(R.drawable.main_screen_note_text)
                }
                if (note.entities.any { it is ContentAudio }) {
                    add(R.drawable.main_screen_note_audio)
                }
                if (note.entities.any { it is ContentImage }) {
                    add(R.drawable.main_screen_note_image)
                }
                putExtra(Extras.EXTRA_NOTE_CONTENT, this)
            }
        }
    }

    override fun setType(intent: Intent, note: Note) {
        intent.apply {
            note.entities.firstOrNull { (it is ContentNote) or (it is ContentImage) or (it is ContentList) }?.apply {
                putExtra(Extras.EXTRA_LAYOUT_TYPE, when (this) {
                    is ContentNote -> R.layout.notification_note
                    is ContentImage -> R.layout.notification_image
                    is ContentList -> R.layout.notification_list
                    else -> R.layout.notification_note
                })
                when {
                    this is ContentList -> putExtra(Extras.EXTRA_NOTE_LIST,
                            arrayListOf<String>().apply { items.take(3).map { it.item }.forEach { add(it) } })
                    this is ContentNote -> putExtra(Extras.EXTRA_NOTE_TEXT, this.note)
                }
            }
        }
    }

    override fun setReminderType(intent: Intent, note: Note) {
        intent.putExtra(Extras.EXTRA_REMINDER_TYPE, note.params!!.type)
    }

    override fun setDefaults(intent: Intent, note: Note) {
        intent.apply {
            putExtra(Extras.EXTRA_NOTE_ID, note.id)
            putExtra(Extras.EXTRA_NOTE_TITLE, (note.entities.first { it is ContentName } as ContentName).name)
            putExtra(Extras.EXTRA_NOTE_TIME, note.params?.time)
            putExtra(Extras.EXTRA_NOTE_DATE, note.params?.date)
        }
    }

    private fun scheduleAlarm(note: Note, delayed: Boolean) {

    }

    override fun cancel(note: Note, onCancel: () -> Unit) {
        note.params?.apply {
            type = Types.NOTE_TYPE_NO_SIGNAL
            timeMillis = 0
            weekDays = RealmList()
            repeatingInterval = 0L
            repeatingPeriod = 0L
            repeatingIntervalPair = Pair(0, "")
            time = "0"
            date = "0"
        }
        alarmManager().apply {
            cancel(PendingIntent.getBroadcast(context, note.id.toInt(),
                    noteIntent(Types.NOTE_TYPE_NOTIFICATION), note.id.toInt()))
            cancel(PendingIntent.getBroadcast(context, note.id.toInt(),
                    noteIntent(Types.NOTE_TYPE_ALARM), note.id.toInt()))
        }
        onCancel()
    }

    private fun noteIntent(type: Int): Intent {
        return Intent(Extras.ACTION_NOTIFICATION)
                .setClass(context,
                        if (type == Types.NOTE_TYPE_NOTIFICATION) NotificationsReceiver::class.java
                        else AlarmsReceiver::class.java)
    }

    private fun alarmManager(): AlarmManager {
        return context.getSystemService(Context.ALARM_SERVICE) as AlarmManager
    }
}