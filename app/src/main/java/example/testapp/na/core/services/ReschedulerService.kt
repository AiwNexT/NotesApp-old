package example.testapp.na.core.services

import android.content.Context
import android.content.Intent
import android.support.v4.app.JobIntentService
import example.testapp.na.core.inject.component.DaggerAppComponent
import example.testapp.na.core.managers.app.ScheduleHelper
import example.testapp.na.core.managers.app.SchedulingUpdatesManager
import example.testapp.na.data.entities.notes.Note
import example.testapp.na.data.entities.notes.NotesRepo
import example.testapp.na.data.preferences.PreferencesHelper
import example.testapp.na.tools.credentials.Requests
import rc.extensions.scope.RCScope
import rc.extensions.streaming.Subscription
import rc.extensions.workers.Workers
import javax.inject.Inject

class ReschedulerService: JobIntentService(), RCScope {

    @Inject lateinit var scheduleHelper: ScheduleHelper

    @Inject lateinit var notesRepo: NotesRepo

    @Inject lateinit var preferences: PreferencesHelper

    private var isWorkFinished = false

    companion object {
        fun enqueueWork(context: Context, work: Intent) {
            enqueueWork(context, ReschedulerService::class.java, Requests.JOB_ID, work)
        }
    }

    override fun onCreate() {
        super.onCreate()

        bind()
    }

    override fun onHandleWork(intent: Intent) {
        reschedule()
    }

    override fun onStopCurrentWork(): Boolean {
        return isWorkFinished
    }

    private fun reschedule() {
        notesRepo.getNotes(false).subscribe(object : Subscription<ArrayList<Note>> {
            override fun onComplete(result: ArrayList<Note>) {
                var count = 0
                result.forEach {
                    scheduleHelper.schedule(it, it.params!!.delayedMillis != 0L, {
                        notesRepo.updateNote(it).doOnComplete {
                            scheduleHelper.startOperation(it.id)
                            SchedulingUpdatesManager.addId(it.id)
                            count++
                            if (count == result.size) {
                                preferences.setTurnedOff(false)
                                isWorkFinished = true
                            }
                        }.execute()
                    })
                }
            }
        }, Workers.merged(8)).execute()
    }

    private fun bind() {
        DaggerAppComponent.builder()
                .application(application)
                .build().inject(this)
    }
}