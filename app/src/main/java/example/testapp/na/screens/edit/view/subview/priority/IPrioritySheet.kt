package example.testapp.na.screens.edit.view.subview.priority

import android.view.View

interface IPrioritySheet {

    fun setUpPriorities(view: View)

    fun onPriorityClicked(which: Int)
}