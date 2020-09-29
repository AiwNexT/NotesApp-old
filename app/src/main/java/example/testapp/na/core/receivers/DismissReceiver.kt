package example.testapp.na.core.receivers

import android.content.Context
import android.content.Intent
import example.testapp.na.tools.credentials.Extras

class DismissReceiver: BaseReceiver() {
    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        component.inject(this)

        if (intent?.getBooleanExtra(Extras.EXTRA_TYPE_DISMISS, false)!!) {
            notificationHelper.dismiss(intent)
            dismiss(context!!, intent)
        }
    }
}