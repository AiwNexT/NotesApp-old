package example.testapp.na.core.inject.module

import android.app.Application
import android.content.Context
import dagger.Module
import dagger.Provides
import example.testapp.na.R
import example.testapp.na.core.inject.keys.PreferencesName
import example.testapp.na.core.managers.app.*
import example.testapp.na.data.entities.notes.NotesRepo
import example.testapp.na.data.entities.notes.NotesRepository
import example.testapp.na.data.preferences.AppPreferences
import example.testapp.na.data.preferences.PreferencesHelper
import example.testapp.na.tools.credentials.Credentials
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import javax.inject.Singleton

@Module
class AppModule {

    @Provides
    @Singleton
    fun provideNotesRepoHelper(notesRepository: NotesRepository): NotesRepo = notesRepository

    @Provides
    @Singleton
    fun provideContext(application: Application): Context = application

    @Provides
    @PreferencesName
    fun providePreferencesKey() = Credentials.PREFERENCES_NAME

    @Provides
    @Singleton
    fun providePreferencesHelper(appPreferences: AppPreferences): PreferencesHelper = appPreferences

    @Provides
    @Singleton
    fun provideRoutingHelper(routingManager: RoutingManager): RoutingHelper = routingManager

    @Provides
    @Singleton
    fun provideFonts(): CalligraphyConfig =
            CalligraphyConfig.Builder()
                    .setDefaultFontPath("fonts/Roboto-Light.ttf")
                    .setFontAttrId(R.attr.fontPath)
                    .build()

    @Provides
    @Singleton
    fun provideAnimationsHelper(animationsManager: AnimationsManager): AnimationsHelper = animationsManager

    @Provides
    @Singleton
    fun provideSnackbarHelper(snackbarManager: SnackbarManager): SnackbarHelper = snackbarManager

    @Provides
    @Singleton
    fun provideSheetManager(sheetManager: SheetManager): SheetHelper = sheetManager

    @Provides
    @Singleton
    fun provideDatePicker(timeDateTimePickerManager: DateTimePickerManager):
            DateTimePickerHelper = timeDateTimePickerManager

    @Provides
    @Singleton
    fun provideUpdatesHelper(updatesSubscriber: UpdatesSubscriber): UpdatesHelper = updatesSubscriber

    @Provides
    @Singleton
    fun provideScheduler(scheduleManager: ScheduleManager): ScheduleHelper = scheduleManager
}