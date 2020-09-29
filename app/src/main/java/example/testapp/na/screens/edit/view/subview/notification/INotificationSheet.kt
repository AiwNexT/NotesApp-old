package example.testapp.na.screens.edit.view.subview.notification

import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import example.testapp.na.vipercore.view.DialogView

interface INotificationSheet: DialogView {

    var updateBeforeDismiss: Boolean

    fun bindSlider(view: View)

    fun buildAdapter(): FragmentPagerAdapter

    fun mainSettings()

    fun repeatingSettings()

    fun provideRoot(): View?

    fun updateNoteOnDismiss()

    fun dismissNoUpdate()
}