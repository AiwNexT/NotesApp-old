package example.testapp.na.core.managers.support

interface ISharedCalendar {

    var timeListener: ((String) -> Unit)?

    var dateListener: ((String) -> Unit)?

    fun setTime(hour: Int, minute: Int)

    fun setDate(year: Int, month: Int, day: Int)

    fun setMillis(millis: Long)

    fun getMillis(): Long

    fun attachTimeListener(time: (String) -> Unit)

    fun attachDateListener(date: (String) -> Unit)

    fun getStringTime(): String

    fun getStringDate(): String
}