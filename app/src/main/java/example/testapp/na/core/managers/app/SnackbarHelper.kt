package example.testapp.na.core.managers.app

import android.support.design.widget.Snackbar
import android.view.View

interface SnackbarHelper {

    fun show(root: View, message: String, onDismissed: () -> Unit, duration: Int = Snackbar.LENGTH_SHORT, action: () -> Unit = {}, actionMsg: String = "")
}