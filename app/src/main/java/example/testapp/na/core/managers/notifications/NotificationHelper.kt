package example.testapp.na.core.managers.notifications

import android.app.Notification
import android.content.Context
import android.content.Intent
import kotlinx.coroutines.experimental.Deferred

interface NotificationHelper {

    fun buildNotification(context: Context, intent: Intent): Deferred<Notification>

    fun delay(intent: Intent, type: Int)

    fun dismiss(intent: Intent)

    fun updateState(intent: Intent)
}