package example.testapp.na.core.managers.app

import android.app.FragmentManager
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import rc.extensions.scope.RCScope
import rc.extensions.scope.worker
import rc.extensions.workers.Workers
import java.util.*
import javax.inject.Inject

class DateTimePickerManager @Inject constructor(): DateTimePickerHelper, RCScope {

    override fun startTimePicker(fMgr: FragmentManager,
                                 listener: TimePickerDialog.OnTimeSetListener) {
        var dialog: TimePickerDialog? = null
        worker {
            Calendar.getInstance().apply {
                dialog = TimePickerDialog.newInstance(
                        listener,
                        get(Calendar.HOUR),
                        get(Calendar.MINUTE),
                        true).apply {
                    isThemeDark = true
                }
            }
        }.doOnComplete { dialog?.show(fMgr, "") }
                .completeOn(Workers.ui())
                .execute()
    }

    override fun startDatePicker(fMgr: FragmentManager,
                                 listener: DatePickerDialog.OnDateSetListener) {
        var dialog: DatePickerDialog? = null
        worker {
            Calendar.getInstance().apply {
                dialog = DatePickerDialog.newInstance(
                        listener,
                        get(Calendar.YEAR),
                        get(Calendar.MONTH),
                        get(Calendar.DAY_OF_MONTH)).apply {
                    minDate = Calendar.getInstance().apply { timeInMillis = System.currentTimeMillis() }
                    isThemeDark = true
                }
            }
        }.completeOn(Workers.ui())
                .doOnComplete { dialog?.show(fMgr, "") }
                .execute()
    }
}