package example.testapp.na.core.managers.app

import android.support.design.widget.Snackbar
import android.view.View
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import javax.inject.Inject

class SnackbarManager @Inject constructor(): SnackbarHelper {

    override fun show(root: View, message: String, onDismissed: () -> Unit, duration: Int, action: () -> Unit, actionMsg: String) {

        launch(UI) { Snackbar.make(root, message, duration)
                .setAction(actionMsg, {
                    action()
                }).addCallback(object : Snackbar.Callback() {
                    override fun onDismissed(transientBottomBar: Snackbar?, event: Int) {
                        (event != DISMISS_EVENT_CONSECUTIVE).let { if (it) onDismissed() }
                    }
                })
                .show() }
    }
}