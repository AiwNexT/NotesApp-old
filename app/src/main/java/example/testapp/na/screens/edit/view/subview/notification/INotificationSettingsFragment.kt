package example.testapp.na.screens.edit.view.subview.notification

import android.view.View
import example.testapp.na.vipercore.view.FragmentView

interface INotificationSettingsFragment: FragmentView {

    fun onControlsClicked(src: View)

    fun onTypeSliderClicked(src: View)

    fun onDismissReminder(src: View)

    fun updateTime(time: String)

    fun updateDate(date: String)

    fun attachListeners()

    fun changeTypeSelection(which: Int)

    fun initFields()
}