package example.testapp.na.core.app

import example.testapp.na.core.inject.component.AppComponent

interface IApp {

    var appComponent: AppComponent

    fun injectMultidex()
    fun inject()

    fun initFonts()

    fun initDb()

    fun registerChannels()

    fun reschedule()
}