package example.testapp.na.core.managers.app

import android.support.v4.app.FragmentManager

interface SheetHelper {

    fun showPriorityPicker(fMgr: FragmentManager)

    fun showNotificationPickerSheet(fMgr: FragmentManager)
}