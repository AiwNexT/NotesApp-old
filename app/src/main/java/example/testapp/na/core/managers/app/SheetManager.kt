package example.testapp.na.core.managers.app

import android.support.v4.app.FragmentManager
import example.testapp.na.vipercore.view.BaseSheet
import example.testapp.na.screens.edit.view.subview.notification.NotificationSheet
import example.testapp.na.screens.edit.view.subview.priority.PrioritySheet
import javax.inject.Inject

class SheetManager @Inject constructor(): SheetHelper {

    override fun showPriorityPicker(fMgr: FragmentManager) {
        build(PrioritySheet(), fMgr, false)
    }

    override fun showNotificationPickerSheet(fMgr: FragmentManager) {
        build(NotificationSheet(), fMgr, true)
    }

    private fun build(sheet: BaseSheet,
                      fMgr: FragmentManager,
                      cancellable: Boolean) {
        sheet.apply {
            isCancelable = cancellable
            show(fMgr, "")
        }
    }
}