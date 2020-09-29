package example.testapp.na.data.preferences

interface PreferencesHelper {

    fun isFirstLaunch(): Boolean
    fun setFirstLaunch(value: Boolean)

    fun getViewType(): Int
    fun setViewType(value: Int)

    fun isNightMode(): Boolean
    fun setNightMode(value: Boolean)

    fun getNotificationDelay(): Long
    fun setNotificationDelay(value: Long)

    fun wasTurnedOff(): Boolean
    fun setTurnedOff(value: Boolean)
}