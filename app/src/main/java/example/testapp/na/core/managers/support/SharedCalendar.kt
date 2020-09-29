package example.testapp.na.core.managers.support

import example.testapp.na.tools.credentials.Dates
import java.util.*
import javax.inject.Inject

class SharedCalendar @Inject constructor(): ISharedCalendar {

    private var calendar: Calendar = Calendar.getInstance()

    override var timeListener: ((String) -> Unit)? = null

    override var dateListener: ((String) -> Unit)? = null

    override fun attachTimeListener(time: (String) -> Unit) {
        timeListener = time
    }

    override fun attachDateListener(date: (String) -> Unit) {
        dateListener = date
    }

    override fun setTime(hour: Int, minute: Int) {
        calendar.apply {
            set(Calendar.HOUR, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
        }
        timeString(timeListener)
    }

    override fun setDate(year: Int, month: Int, day: Int) {
        calendar.apply {
            set(Calendar.YEAR, year)
            set(Calendar.MONTH, month)
            set(Calendar.DAY_OF_MONTH, day)
        }
        dateString(dateListener)
    }

    override fun setMillis(millis: Long) {
        calendar.apply {
            clear()
            timeInMillis = millis
        }
    }

    override fun getMillis(): Long {
        return calendar.timeInMillis
    }

    private fun timeString(time: ((String) -> Unit)?) {
        time?.invoke("${normalizedTimeValue(calendar.get(Calendar.HOUR).toString())}:" +
                normalizedTimeValue(calendar.get(Calendar.MINUTE).toString()))
    }

    private fun dateString(date: ((String) -> Unit)?) {
        date?.invoke("${calendar.get(Calendar.DAY_OF_MONTH)} " +
                "${Dates.dates.first { it.first == calendar.get(Calendar.MONTH) }.second} " +
                "${calendar.get(Calendar.YEAR)}")
    }

    override fun getStringDate(): String {
        calendar.let {
            return "${it.get(Calendar.DAY_OF_MONTH)} " +
                    "${Dates.dates.first { it.first == calendar.get(Calendar.MONTH) }.second} " +
                    "${it.get(Calendar.YEAR)}"
        }
    }

    override fun getStringTime(): String {
        calendar.let {
            return "${normalizedTimeValue(it.get(Calendar.HOUR).toString())}:" +
                    normalizedTimeValue(it.get(Calendar.MINUTE).toString())
        }
    }

    private fun normalizedTimeValue(value: String): String {
        return value.let { if (value.length == 1) "0$value" else value }
    }
}