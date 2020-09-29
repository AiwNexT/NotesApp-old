package example.testapp.na.core.managers.app

import android.app.FragmentManager
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog

interface DateTimePickerHelper {

    fun startTimePicker(fMgr: FragmentManager, listener: TimePickerDialog.OnTimeSetListener)

    fun startDatePicker(fMgr: FragmentManager, listener: DatePickerDialog.OnDateSetListener)
}