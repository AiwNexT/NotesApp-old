package example.testapp.na.core.inject.component

import android.app.Application
import android.content.Context
import dagger.BindsInstance
import dagger.Component
import example.testapp.na.core.app.App
import example.testapp.na.core.inject.module.AppModule
import example.testapp.na.core.managers.app.*
import example.testapp.na.core.services.ReschedulerService
import example.testapp.na.data.entities.notes.NotesRepo
import example.testapp.na.data.preferences.PreferencesHelper
import javax.inject.Singleton

@Singleton
@Component(modules = [(AppModule::class)])
interface AppComponent {

    @Component.Builder
    interface Builder {

        @BindsInstance
        fun application(application: Application): Builder

        fun build(): AppComponent
    }

    fun notes(): NotesRepo
    fun prefs(): PreferencesHelper
    fun routing(): RoutingHelper
    fun animations(): AnimationsHelper
    fun snacks(): SnackbarHelper
    fun sheets(): SheetHelper
    fun dateTime(): DateTimePickerHelper
    fun updates(): UpdatesHelper
    fun scheduler(): ScheduleHelper
    fun appContext(): Context

    fun inject(app: App)
    fun inject(service: ReschedulerService)
}