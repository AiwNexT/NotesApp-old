package example.testapp.na.screens.edit

import dagger.Module
import dagger.Provides
import example.testapp.na.core.inject.scopes.Support
import example.testapp.na.core.managers.support.ISharedCalendar
import example.testapp.na.core.managers.support.SharedCalendar

@Module
class EditSupportModule {

    @Support
    @Provides
    fun provideSharedCalendar(sharedCalendar: SharedCalendar): ISharedCalendar = sharedCalendar
}