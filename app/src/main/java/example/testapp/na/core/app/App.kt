package example.testapp.na.core.app

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.support.multidex.MultiDex
import android.support.multidex.MultiDexApplication
import example.testapp.na.core.inject.component.AppComponent
import example.testapp.na.core.inject.component.DaggerAppComponent
import example.testapp.na.core.receivers.RebootReceiver
import example.testapp.na.tools.credentials.Extras
import io.realm.Realm
import uk.co.chrisjenx.calligraphy.CalligraphyConfig
import javax.inject.Inject

class App: MultiDexApplication(), IApp {

    @Inject lateinit var fontsConfig: CalligraphyConfig

    override lateinit var appComponent: AppComponent

    override fun onCreate() {
        super.onCreate()
        inject()
        initFonts()
        initDb()
        registerChannels()
        reschedule()
    }

    override fun injectMultidex() {
        MultiDex.install(this)
    }

    override fun inject() {
        appComponent = DaggerAppComponent.builder()
                .application(this)
                .build()
        appComponent.inject(this)
    }

    override fun initFonts() {
        CalligraphyConfig.initDefault(fontsConfig)
    }

    override fun initDb() {
        Realm.init(this)
    }

    override fun reschedule() {
        startService(Intent(this, RebootReceiver::class.java))
    }

    override fun registerChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            (applicationContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager)
                    .createNotificationChannel(NotificationChannel(Extras.ACTION_NOTIFICATION,
                            Extras.ACTION_NOTIFICATION,
                            NotificationManager.IMPORTANCE_DEFAULT))
        }
    }
}