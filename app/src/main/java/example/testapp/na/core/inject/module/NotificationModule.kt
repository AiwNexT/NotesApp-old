package example.testapp.na.core.inject.module

import dagger.Module
import dagger.Provides
import example.testapp.na.core.inject.scopes.Notification
import example.testapp.na.core.managers.notifications.NotificationContentHelper
import example.testapp.na.core.managers.notifications.NotificationContentManager
import example.testapp.na.core.managers.notifications.NotificationHelper
import example.testapp.na.core.managers.notifications.NotificationManager

@Module
class NotificationModule {

    @Notification
    @Provides
    fun provideNotificationsHelper(notificationsManager: NotificationManager)
            : NotificationHelper = notificationsManager

    @Notification
    @Provides
    fun provideNotificationsContentHelper(notificationContentManager: NotificationContentManager)
            : NotificationContentHelper = notificationContentManager
}