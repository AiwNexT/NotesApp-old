package example.testapp.na.screens.edit.view.subview.notification

import android.view.View
import example.testapp.na.vipercore.view.FragmentView

interface IRepeatingNotificationSettingsFragment: FragmentView {

    fun initPager()

    fun changePageTabState(position: Int)

    fun tabsClick(v: View)
}