package example.testapp.na.screens.edit.view.subview.notification

import android.view.View
import example.testapp.na.vipercore.view.FragmentView

interface IWeekdaysRepeating: FragmentView {

    fun cleanupViews()

    fun onSelected(v: View)
}