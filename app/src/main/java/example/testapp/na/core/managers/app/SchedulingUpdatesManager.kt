package example.testapp.na.core.managers.app

import kotlinx.coroutines.experimental.Job
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.sync.Mutex
import kotlinx.coroutines.experimental.sync.withLock
import rc.extensions.workers.Workers
import javax.inject.Inject

class SchedulingUpdatesManager @Inject constructor() : SchedulingUpdatesHelper {

    private var tracker: Job? = null

    private val targets = arrayListOf<(id: Long) -> Unit>()

    private var mutex = Mutex()

    companion object {

        private val iDs = arrayListOf<Long>()

        fun addId(id: Long) {
            iDs.add(id)
        }
    }

    override fun startTracking() {
        tracker = createTracker()
    }

    override fun stopTracking() {
        tracker?.cancel()
    }

    override fun addTarget(target: (id: Long) -> Unit): Int {
        targets.add(target)
        return targets.size - 1
    }

    override fun removeTarget(uniqueId: Int) {
        targets.removeAt(uniqueId)
    }

    private fun createTracker(): Job = launch(Workers.dedicated()) {
        while (true) {
            delay(1000)
            mutex.withLock {
                iDs.forEachIndexed { index, item ->
                    targets.forEach {
                        it.invoke(item)
                    }
                }
                iDs.clear()
            }
        }
    }
}