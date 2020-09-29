package example.testapp.na.core.receivers

import android.content.Context
import android.content.Intent
import example.testapp.na.tools.credentials.Extras
import example.testapp.na.tools.credentials.Types

class DelayReceiver: BaseReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        component.inject(this)

        if (intent?.getBooleanExtra(Extras.EXTRA_TYPE_SNOOZE, false)!!) {
            notificationHelper.delay(intent, intent.getIntExtra(Extras.EXTRA_REMINDER_TYPE, Types.NOTE_TYPE_NOTIFICATION))
            dismiss(context!!, intent)
        }
    }
}