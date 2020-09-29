package example.testapp.na.screens.edit.presenter

import android.net.Uri
import android.support.v7.widget.RecyclerView
import android.view.View
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import example.testapp.na.vipercore.presenter.Presenter
import example.testapp.na.screens.edit.interactor.IEditInteractor
import example.testapp.na.screens.edit.router.IEditRouter
import example.testapp.na.screens.edit.view.IEditAdapter
import example.testapp.na.screens.edit.view.IEditView
import rc.extensions.handlers.single.SingleSubscriber
import rc.extensions.handlers.single.Task

interface IEditPresenter<V: IEditView, I: IEditInteractor, R: IEditRouter>: Presenter<V, I, R> {

    fun attachEditAdapter(adapter: IEditAdapter)

    fun loadNote(id: Long)

    fun getConfig()

    fun newNote(): Task

    fun addNote()

    fun addAudio()

    fun addList()

    fun addListItem(offset: Int, after: Int, add: String = "")

    fun addImage()

    fun onImagePicked(data: Uri)

    fun addTagItem(offset: Int, after: Int, add: String = "")

    fun onEmptyTag()

    fun onClearList(offset: Int)

    fun buildPrioritiesPicker(): SingleSubscriber<ArrayList<Int>>

    fun onPriorityPicked(priority: Int)

    fun onTimeSet(): TimePickerDialog.OnTimeSetListener

    fun onDateSet(): DatePickerDialog.OnDateSetListener

    fun changeRemindType(which: Int, caller: View): Task

    fun onTypeChanged(caller: View)

    fun onDismissRemind(onEnd: () -> Unit)

    fun lockNote()

    fun pinNote()

    fun archiveNote()

    fun saveTimeDateSettings()

    fun checkoutReminderType(onCheckout: (which: Int) -> Unit)

    fun backToNotesList()

    fun onListInit(list: RecyclerView)

    fun removeItem(position: Int)

    fun onWeekdaysUpdate(weekdays: ArrayList<Int>)

    fun loadWeekdays(): SingleSubscriber<ArrayList<Int>>

    fun onIntervalUpdate(interval: Long, period: Long)

    fun loadInterval(): SingleSubscriber<Pair<Long, Long>>
}