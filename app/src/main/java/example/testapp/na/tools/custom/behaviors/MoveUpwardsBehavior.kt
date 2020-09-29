package example.testapp.na.tools.custom.behaviors

import android.annotation.SuppressLint
import android.os.Build
import android.support.design.widget.CoordinatorLayout
import android.support.design.widget.Snackbar
import android.view.View

class MoveUpwardsBehavior : CoordinatorLayout.Behavior<View>() {

    override fun layoutDependsOn(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        return SNACKBAR_BEHAVIOR_ENABLED and (dependency is Snackbar.SnackbarLayout)
    }

    override fun onDependentViewChanged(parent: CoordinatorLayout, child: View, dependency: View): Boolean {
        val translationY = Math.min(0f, dependency.translationY - dependency.height)
        if ((Math.abs(translationY) == dependency.height.toFloat()) and (dependency.x != 0f)) {
            val supportTranslation = -Math.min(dependency.x, dependency.width.toFloat())
                    .let { it / (dependency.width / dependency.height) }
                    .let { dependency.height - it }
                    .let { if (it <= 0f) 0f else it }
            child.translationY = supportTranslation
        } else {
            child.translationY = translationY
        }
        return true
    }

    companion object {
        @SuppressLint("ObsoleteSdkInt")
        private val SNACKBAR_BEHAVIOR_ENABLED: Boolean = Build.VERSION.SDK_INT >= 11
    }
}