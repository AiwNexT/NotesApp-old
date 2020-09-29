package example.testapp.na.tools.custom.views

import android.content.Context
import android.util.AttributeSet
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputConnection
import android.view.inputmethod.InputConnectionWrapper
import android.widget.EditText
import example.testapp.na.tools.credentials.Keys
import kotlinx.coroutines.experimental.*
import kotlinx.coroutines.experimental.android.UI
import rc.extensions.workers.Workers

class ListText : EditText {

    private var onKeyDownListener: ((keyCode: Int) -> Unit)? = null

    constructor(context: Context, attrs: AttributeSet, defStyle: Int) : super(context, attrs, defStyle)
    constructor(context: Context, attrs: AttributeSet) : super(context, attrs)
    constructor(context: Context) : super(context)

    private var looper: Job? = null

    override fun onDetachedFromWindow() {
        looper?.cancel()
        super.onDetachedFromWindow()
    }

    init {
        setOnFocusChangeListener { _, hasFocus ->
            if (hasFocus) {
                createLooperChecker()
            } else {
                looper?.cancel()
            }
        }
    }

    private fun createLooperChecker() {
        looper = launch(newCoroutineContext(Workers.default())) {
            while (true) {
                delay(100)
                withContext(UI) { onKeyDownListener?.invoke(Keys.CODE_FOCUS_EVENT) }
            }
        }
    }

    override fun onCreateInputConnection(outAttrs: EditorInfo): InputConnection {
        return ListInputConnection(super.onCreateInputConnection(outAttrs), true)
    }

    fun setKeyDownListener(listener: (keyCode: Int) -> Unit) {
        this.onKeyDownListener = listener
    }

    fun sendEvent(keyCode: Int) {
        this.onKeyDownListener?.invoke(keyCode)
    }

    override fun onKeyDown(keyCode: Int, event: KeyEvent?): Boolean {
        onKeyDownListener?.invoke(keyCode)
        return super.onKeyDown(keyCode, event)
    }

    private inner class ListInputConnection(target: InputConnection, mutable: Boolean) : InputConnectionWrapper(target, mutable) {

        override fun deleteSurroundingText(beforeLength: Int, afterLength: Int): Boolean {
            sendKeyEvent(KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL)) &&
                    sendKeyEvent(KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL))
            return super.deleteSurroundingText(beforeLength, afterLength)
        }
    }
}