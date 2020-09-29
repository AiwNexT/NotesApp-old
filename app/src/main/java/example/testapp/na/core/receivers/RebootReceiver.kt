package example.testapp.na.core.receivers

import android.content.Context
import android.content.Intent
import example.testapp.na.core.services.ReschedulerService
import example.testapp.na.data.preferences.PreferencesHelper
import javax.inject.Inject

class RebootReceiver: BaseReceiver() {

    @Inject lateinit var preferences: PreferencesHelper

    override fun onReceive(context: Context?, intent: Intent?) {
        super.onReceive(context, intent)

        component.inject(this)

        scheduleAfterReboot()

        ReschedulerService.enqueueWork(context!!, Intent())
    }

    private fun scheduleAfterReboot() {
        preferences.setTurnedOff(true)
    }
}