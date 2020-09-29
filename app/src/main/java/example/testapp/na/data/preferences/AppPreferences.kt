package example.testapp.na.data.preferences

import android.annotation.SuppressLint
import android.content.Context
import android.content.SharedPreferences
import example.testapp.na.core.inject.keys.PreferencesName
import example.testapp.na.tools.credentials.Defaults
import example.testapp.na.tools.credentials.PrefsKeys
import example.testapp.na.tools.credentials.Types
import javax.inject.Inject

class AppPreferences @Inject internal constructor(context: Context, @PreferencesName private val prefsName: String) : PreferencesHelper {

    private val prefs: SharedPreferences = context.getSharedPreferences(prefsName, Context.MODE_PRIVATE)

    override fun isFirstLaunch() = get(PrefsKeys.FIRST_LAUNCH, false)
    override fun setFirstLaunch(value: Boolean) = set(PrefsKeys.FIRST_LAUNCH, true)

    override fun getViewType(): Int = get(PrefsKeys.NOTES_VIEW_TYPE, Types.VIEW_TYPE_GRID)
    override fun setViewType(value: Int) = set(PrefsKeys.NOTES_VIEW_TYPE, value)

    override fun isNightMode(): Boolean = get(PrefsKeys.NIGHT_MODE_ENABLED, false)
    override fun setNightMode(value: Boolean) = set(PrefsKeys.NIGHT_MODE_ENABLED, value)

    override fun getNotificationDelay(): Long = get(PrefsKeys.NOTIFICATION_DELAY, Defaults.DEFAULT_NOTIFICATION_DELAY)
    override fun setNotificationDelay(value: Long) = set(PrefsKeys.NOTIFICATION_DELAY, value)

    override fun wasTurnedOff(): Boolean = get(PrefsKeys.WAS_TURNED_OFF, false)
    override fun setTurnedOff(value: Boolean) = set(PrefsKeys.WAS_TURNED_OFF, value)

    @SuppressLint("CommitPrefEdits")
    fun <T> set(key: String, value: T) {
        prefs.edit().apply {
            when (value) {
                is String -> putString(key, value).apply()
                is Int -> putInt(key, value).apply()
                is Long -> putLong(key, value).apply()
                is Float -> putFloat(key, value).apply()
                is Boolean -> putBoolean(key, value).apply()
                else -> { }
            }
        }
    }

    fun <T> get(key: String, default: T): T {
        prefs.apply {
            return when (default) {
                is String -> getString(key, default) as T
                is Int -> getInt(key, default) as T
                is Long -> getLong(key, default) as T
                is Float -> getFloat(key, default) as T
                is Boolean -> getBoolean(key, default) as T
                else -> { null as T }
            }
        }
        return null as T
    }
}