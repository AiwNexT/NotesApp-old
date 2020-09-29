package example.testapp.na.screens.edit.view.subview.notification

import android.view.View
import example.testapp.na.vipercore.view.FragmentView

interface ICustomRepeating: FragmentView {

    fun cleanupInterval()

    fun onChangeInterval(v: View)

    fun intervalUpdates()
}