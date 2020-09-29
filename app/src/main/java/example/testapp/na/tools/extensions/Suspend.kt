package example.testapp.na.tools.extensions

import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.delay
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.withContext

interface Delayable

fun Delayable.wait(onEnd: () -> Unit) = launch {
    delay(5)
    withContext(UI) {
        onEnd()
    }
}

fun Delayable.wait(millis: Long, onEnd: () -> Unit) = launch {
    delay(millis)
    withContext(UI) {
        onEnd()
    }    
}
