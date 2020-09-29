package example.testapp.na.core.managers.app

import android.view.View

interface AnimationsHelper {

    fun hideObjectByWidth(view: View, onEnd: () -> Unit, disable: Boolean = true)
    fun showObjectByWidth(view: View, amount: Int, onEnd: () -> Unit, disable: Boolean = true)

    fun showObjectByHeight(view: View, amount: Int, onEnd: () -> Unit, disable: Boolean = true)
    fun hideObjectByHeight(view: View, onEnd: () -> Unit, disable: Boolean = true)
    fun hideObjectByHeight(view: View, amount: Int, onEnd: () -> Unit, disable: Boolean = true)

    fun slideObjectTop(view: View, amount: Float, onEnd: () -> Unit, disable: Boolean = true)
    fun slideObjectBottom(view: View, amount: Float, onEnd: () -> Unit, disable: Boolean = true)

    fun scaleObjectFullDown(view: View, onEnd: () -> Unit, disable: Boolean = true)
    fun scaleObjectFullUp(view: View, onEnd: () -> Unit, disable: Boolean = true)
}