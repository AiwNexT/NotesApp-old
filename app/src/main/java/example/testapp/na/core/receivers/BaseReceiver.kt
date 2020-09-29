package example.testapp.na.core.receivers

import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.support.v4.content.WakefulBroadcastReceiver
import example.testapp.na.core.app.App
import example.testapp.na.core.inject.component.DaggerNotificationComponent
import example.testapp.na.core.inject.component.NotificationComponent
import example.testapp.na.core.inject.module.NotificationModule
import example.testapp.na.core.managers.notifications.NotificationHelper
import example.testapp.na.tools.credentials.Extras
import io.realm.Realm
import javax.inject.Inject

abstract class BaseReceiver: WakefulBroadcastReceiver(), IReceiver {

    lateinit var component: NotificationComponent

    @Inject lateinit var notificationHelper: NotificationHelper

    override fun onReceive(context: Context?, intent: Intent?) {
        buildComponent(context!!)
    }

    override fun notificationManager(context: Context): NotificationManager {
        return context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
    }

    override fun buildComponent(context: Context): NotificationComponent {
        Realm.init(context.applicationContext)
        component = DaggerNotificationComponent.builder()
                .appComponent(((context.applicationContext) as App).appComponent)
                .notificationModule(NotificationModule()).build()
        return component
    }

    override fun dismiss(context: Context, intent: Intent) {
        notificationManager(context).cancel(intent.getLongExtra(Extras.EXTRA_NOTE_ID, 0).toInt())
    }
}