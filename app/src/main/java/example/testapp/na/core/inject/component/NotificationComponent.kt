package example.testapp.na.core.inject.component

import dagger.Component
import example.testapp.na.core.inject.module.NotificationModule
import example.testapp.na.core.inject.scopes.Notification
import example.testapp.na.core.receivers.AlarmsReceiver
import example.testapp.na.core.receivers.BaseReceiver
import example.testapp.na.core.receivers.NotificationsReceiver
import example.testapp.na.core.receivers.RebootReceiver

@Notification
@Component(modules = [NotificationModule::class], dependencies = [AppComponent::class])
interface NotificationComponent {

    fun inject(receiver: BaseReceiver)

    fun inject(receiver: NotificationsReceiver)
    fun inject(receiver: AlarmsReceiver)
    fun inject(receiver: RebootReceiver)
}