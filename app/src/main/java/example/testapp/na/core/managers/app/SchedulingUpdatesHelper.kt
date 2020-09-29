package example.testapp.na.core.managers.app

interface SchedulingUpdatesHelper {

    fun startTracking()

    fun stopTracking()

    fun addTarget(target: (id: Long) -> Unit): Int

    fun removeTarget(uniqueId: Int)
}