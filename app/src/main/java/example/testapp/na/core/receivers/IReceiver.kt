package example.testapp.na.core.receivers

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import example.testapp.na.core.inject.component.NotificationComponent


interface IReceiver {

    fun notificationManager(context: Context): NotificationManager

    fun buildComponent(context: Context): NotificationComponent

    fun dismiss(context: Context, intent: Intent)
}