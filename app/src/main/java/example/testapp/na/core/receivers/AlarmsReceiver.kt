package example.testapp.na.core.receivers

import android.content.Context
import android.content.Intent

class AlarmsReceiver: BaseReceiver() {

    override fun onReceive(context: Context?, intent: Intent?) {
        println("NA: Alarm!")

        super.onReceive(context, intent)
    }
}