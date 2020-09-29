package example.testapp.na.core.managers.app

import android.animation.Animator
import android.view.View
import com.bartoszlipinski.viewpropertyobjectanimator.ViewPropertyObjectAnimator
import example.testapp.na.tools.credentials.Durations
import javax.inject.Inject

class AnimationsManager @Inject constructor(): AnimationsHelper {

    override fun hideObjectByWidth(view: View, onEnd: () -> Unit, disable: Boolean) {
        ViewPropertyObjectAnimator.animate(view).width(0).setDuration(Durations.OBJECT_WIDTH_CHANGE_DURATION)
                .addListener(listener(view, disable, onEnd)).start()
    }

    override fun showObjectByWidth(view: View, amount: Int, onEnd: () -> Unit, disable: Boolean) {
        ViewPropertyObjectAnimator.animate(view).width(amount).setDuration(Durations.OBJECT_WIDTH_CHANGE_DURATION)
                .addListener(listener(view, disable, onEnd)).start()
    }

    override fun showObjectByHeight(view: View, amount: Int, onEnd: () -> Unit, disable: Boolean) {
        ViewPropertyObjectAnimator.animate(view).height(amount).setDuration(Durations.OBJECT_WIDTH_CHANGE_DURATION)
                .addListener(listener(view, disable, onEnd)).start()
    }

    override fun hideObjectByHeight(view: View, onEnd: () -> Unit, disable: Boolean) {
        hideObjectByHeight(view, 0, onEnd, disable)
    }

    override fun hideObjectByHeight(view: View, amount: Int, onEnd: () -> Unit, disable: Boolean) {
        ViewPropertyObjectAnimator.animate(view).height(amount).setDuration(Durations.OBJECT_WIDTH_CHANGE_DURATION)
                .addListener(listener(view, disable, onEnd)).start()
    }

    override fun slideObjectTop(view: View, amount: Float, onEnd: () -> Unit, disable: Boolean) {
        view.y(-amount, Durations.SLIDE_DURATION, onEnd, disable)
    }

    override fun slideObjectBottom(view: View, amount: Float, onEnd: () -> Unit, disable: Boolean) {
        view.y(amount, Durations.SLIDE_DURATION, onEnd, disable)
    }

    override fun scaleObjectFullDown(view: View, onEnd: () -> Unit, disable: Boolean) {
        view.scale(0f, 0f, Durations.SCALE_DURATION, onEnd, disable)
    }

    override fun scaleObjectFullUp(view: View, onEnd: () -> Unit, disable: Boolean) {
        view.scale(1f, 1f, Durations.SCALE_DURATION, onEnd, disable)
    }

    private fun View.scale(toX: Float, toY: Float, duration: Long, onEnd: () -> Unit, disable: Boolean) {
        animate().setDuration(duration).scaleX(toX).scaleY(toY)
                .setListener(listener(this, disable, onEnd)).start()
    }

    private fun View.y(toY: Float, duration: Long, onEnd: () -> Unit, disable: Boolean) {
        animate().setDuration(duration).yBy(toY)
                .setListener(listener(this, disable, onEnd)).start()
    }

    private fun listener(view: View, disable: Boolean, onEnd: () -> Unit) = object : Animator.AnimatorListener {
        override fun onAnimationRepeat(animation: Animator?) {}
        override fun onAnimationEnd(animation: Animator?) {
            if (disable) { view.isClickable = true }
            onEnd.invoke()
        }
        override fun onAnimationCancel(animation: Animator?) {}
        override fun onAnimationStart(animation: Animator?) {
            if (disable) { view.isClickable = false }
        }
    }
}