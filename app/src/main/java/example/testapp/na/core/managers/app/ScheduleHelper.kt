package example.testapp.na.core.managers.app

import android.content.Intent
import example.testapp.na.data.entities.notes.Note

interface ScheduleHelper {

    fun schedule(note: Note, delayed: Boolean, onScheduled: () -> Unit)

    fun startOperation(id: Long)

    fun cancel(note: Note, onCancel: () -> Unit)

    fun setDefaults(intent: Intent, note: Note)

    fun setType(intent: Intent, note: Note)

    fun setReminderType(intent: Intent, note: Note)

    fun setContent(intent: Intent, note: Note)

    fun setRepeating(intent: Intent, note: Note)

    fun setOneOff(intent: Intent, note: Note)

    fun getRepeatingTime(note: Note, millis: Long): Long
}