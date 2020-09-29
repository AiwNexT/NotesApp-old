package example.testapp.na.core.inject.component

import dagger.Component
import example.testapp.na.vipercore.view.BaseActivity
import example.testapp.na.vipercore.view.BaseDialog
import example.testapp.na.vipercore.view.BaseFragment
import example.testapp.na.vipercore.view.BaseSheet
import example.testapp.na.core.inject.scopes.Support
import example.testapp.na.core.managers.support.NoteItemViewManager
import example.testapp.na.screens.edit.EditModule
import example.testapp.na.screens.edit.EditSupportModule
import example.testapp.na.screens.edit.view.EditActivity
import example.testapp.na.screens.edit.view.subview.notification.*
import example.testapp.na.screens.edit.view.subview.priority.PrioritySheet
import example.testapp.na.screens.main.MainModule
import example.testapp.na.screens.main.view.MainActivity

@Support
@Component(dependencies = [(AppComponent::class)],
        modules = [(MainModule::class), (EditModule::class), (EditSupportModule::class)])
interface SupportComponent {

    //ActivityView
    fun inject(activity: BaseActivity)
    fun inject(activity: MainActivity)
    fun inject(activity: EditActivity)

    //DialogView/SheetView
    fun inject(sheet: BaseSheet)
    fun inject(sheet: PrioritySheet)
    fun inject(sheet: NotificationSheet)

    fun inject(dialog: BaseDialog)

    //FragmentView
    fun inject(fragment: BaseFragment)
    fun inject(fragment: NotificationSettingsFragment)
    fun inject(fragment: RepeatingNotificationSettingsFragment)
    fun inject(fragment: WeekdaysRepeatingFragment)
    fun inject(fragment: CustomRepeatingFragment)

    //Non-architecture
    fun inject(itemViewManager: NoteItemViewManager)
}