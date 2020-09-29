package example.testapp.na.vipercore.view

import android.content.Context

interface View {

    val viewID: Int

    fun onViewCreated() {}

    fun routingContext(): Context? = null

    fun setUpAnimations() {}
}