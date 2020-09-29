package example.testapp.na.core.receivers

import android.content.Context
import android.content.Intent
import example.testapp.na.tools.credentials.Extras
import kotlinx.coroutines.experimental.launch

class NotificationsReceiver : BaseReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)
        println("NA: Notific!")

        component.inject(this)

        launch {
            val notification = notificationHelper.buildNotification(context!!, intent!!).await()

            notificationManager(context)
                    .notify(intent.getLongExtra(Extras.EXTRA_NOTE_ID, 0).toInt(), notification)
        }
    }
}