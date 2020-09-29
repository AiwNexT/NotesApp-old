package example.testapp.na.screens.edit.presenter

import android.app.Activity
import android.net.Uri
import android.provider.MediaStore
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.View
import com.loopeer.itemtouchhelperextension.ItemTouchHelperExtension
import com.wdullaer.materialdatetimepicker.date.DatePickerDialog
import com.wdullaer.materialdatetimepicker.time.TimePickerDialog
import example.testapp.na.core.managers.app.ScheduleHelper
import example.testapp.na.vipercore.presenter.BasePresenter
import example.testapp.na.core.managers.app.UpdatesHelper
import example.testapp.na.core.managers.support.ISharedCalendar
import example.testapp.na.data.entities.notes.Note
import example.testapp.na.data.entities.notes.notescontent.*
import example.testapp.na.data.entities.support.ListItem
import example.testapp.na.data.entities.support.Params
import example.testapp.na.screens.edit.dto.ConfigEdit
import example.testapp.na.screens.edit.interactor.IEditInteractor
import example.testapp.na.screens.edit.router.IEditRouter
import example.testapp.na.screens.edit.view.IEditAdapter
import example.testapp.na.screens.edit.view.IEditView
import example.testapp.na.tools.credentials.Messages
import example.testapp.na.tools.credentials.Priorities
import example.testapp.na.tools.credentials.Types
import example.testapp.na.tools.extensions.compress
import example.testapp.na.tools.extensions.offset
import example.testapp.na.tools.extensions.toBase64
import io.realm.RealmList
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import rc.extensions.handlers.single.SingleSubscriber
import rc.extensions.handlers.single.Task
import rc.extensions.scope.*
import rc.extensions.streaming.Subscription
import rc.extensions.workers.Workers
import javax.inject.Inject
import example.testapp.na.tools.custom.views.ListCallbackController
import example.testapp.na.tools.extensions.swap


class EditPresenter<V : IEditView, I : IEditInteractor, R : IEditRouter>
@Inject internal constructor(private val updatesHelper: UpdatesHelper,
                             private val sharedCalendar: ISharedCalendar,
                             private val scheduleHelper: ScheduleHelper,
                             interactor: I, router: R) :
        BasePresenter<V, I, R>(interactor = interactor, router = router),
        IEditPresenter<V, I, R>, RCScope {

    private lateinit var editAdapter: IEditAdapter

    private lateinit var config: ConfigEdit

    private lateinit var itemTouchHelperExtension: ItemTouchHelperExtension

    private var note: Note? = null

    override fun onAttach(view: V?) {
        super.onAttach(view)
        receiveExtras()
    }

    override fun attachEditAdapter(adapter: IEditAdapter) {
        this.editAdapter = adapter
        getConfig()
    }

    override fun onListInit(list: RecyclerView) {
        val itemTouchHelperCallback = ListCallbackController(object :
                ListCallbackController.RecyclerItemTouchHelperListener {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {}
            override fun onMoved(from: Int, to: Int) {
                if ((from >= 2) and (from < note?.entities?.size!! - 1) and (to >= 2) and (to < note?.entities?.size!! - 1)) {
                    note?.entities?.swap(from, to)
                    editAdapter.itemMoved(from, to)
                }
            }
        }, ItemTouchHelper.START)
        itemTouchHelperExtension = ItemTouchHelperExtension(itemTouchHelperCallback).apply {
            attachToRecyclerView(list)
        }
    }

    override fun getConfig() {
        interactor.loadConfig { }
                .completeOn(Workers.default())
                .subscribe(object : Subscription<ConfigEdit> {
                    override fun onComplete(result: ConfigEdit) {
                        applyConfig(result)
                    }
                }, Workers.dedicated())
                .execute()
    }

    private fun applyConfig(configEdit: ConfigEdit) {
        this.config = configEdit
        if (configEdit.editMode) {
            loadNote(configEdit.noteId)
        } else {
            newNote().doOnComplete {
                displayNote()
                if (config.withAlarmConfig) {
                    view?.onNotificationClicked()
                }
            }.completeOn(Workers.ui()).execute()
        }
    }

    private fun displayNote() = launch(UI) {
        editAdapter.attachNote(note!!)
        editAdapter.onNoteCreated()
        editAdapter.itemsRangeInserted(0, note?.entities!!.size)
    }

    override fun loadNote(id: Long) {
        interactor.loadNote(id)
                .subscribe(object : Subscription<Note> {
                    override fun onComplete(result: Note) {
                        note = result
                        displayNote()
                        onNoteLocked()
                        onNotePinned()
                        editCalendar()
                        view?.changeNotificationState(note?.params?.timeMillis != 0L)
                    }
                }, Workers.ui()).execute()
    }

    private fun editCalendar() {
        if ((note?.params!!.timeMillis == 0L) and
                (note?.params?.type!! == Types.NOTE_TYPE_NO_SIGNAL)) {
            sharedCalendar.setMillis(0L)
        } else {
            sharedCalendar.setMillis(note?.params?.timeMillis!!)
        }
    }

    override fun newNote(): Task {
        return worker {
            note = Note().apply {
                interactor.createNoteId(object : Subscription<Long> {
                    override fun onComplete(result: Long) {
                        (this@apply).id = result
                    }
                }).execute()
                entities = arrayListOf()
                entities.add(ContentPriority(Priorities.MEDIUM))
                entities.add(ContentName(""))
                if (config.withEmptyNote) {
                    entities.add(ContentNote(""))
                }
                if (config.withEmptyList) {
                    entities.add(ContentList(arrayListOf(ListItem("", false))))
                }
                if (config.withAudioRecord) {
                    entities.add(ContentAudio(""))
                }
                entities.add(ContentTags(arrayListOf("")))
                params = createEmptyParams()
                if (config.withAlarmConfig) {
                    params!!.type = Types.NOTE_TYPE_ALARM
                }
                sharedCalendar.setMillis(0L)
            }
        }
    }

    private fun createEmptyParams() =
            Params(Types.NOTE_TYPE_NO_SIGNAL,
                    0, 0, false, false,
                    "0", "0", RealmList(),
                    0, 0, Pair(0, ""))

    private fun receiveExtras() {
        interactor.loadExtras((view as Activity).intent.extras)
    }

    override fun addNote() {
        notifyAboutInsertion { note?.entities?.add(ContentNote("")) }
    }

    override fun addList() {
        notifyAboutInsertion {
            note?.entities?.add(ContentList(arrayListOf(ListItem("", false))))
        }
    }

    override fun addListItem(offset: Int, after: Int, add: String) {
        (note?.entities!![offset] as ContentList).items.apply {
            if (after == size) {
                add(ListItem(add, false))
            } else {
                add(after + 1, ListItem(add, false))
            }
            editAdapter.itemUpdatedWithInsertion(offset, after + 1)
        }
    }

    override fun addAudio() {
        notifyAboutInsertion { note?.entities?.add(ContentAudio("")) }
    }

    override fun addImage() {
        worker {
            if (note?.entities?.filter { it is ContentImage }?.size!! < 20) {
                ui { router.startImagePicker() }.execute()
            } else {
                ui { view?.showSnack(Messages.IMAGES_LIMIT) }.execute()
            }
        }.execute()
    }

    override fun addTagItem(offset: Int, after: Int, add: String) {
        (note?.entities!![offset] as ContentTags).tags.apply {
            add(after + 1, add)
            editAdapter.itemUpdatedWithInsertion(offset, after + 1)
        }
    }

    override fun onImagePicked(data: Uri) {
        merged(8) {
            MediaStore.Images.Media.getBitmap((view as Activity).contentResolver, data).apply {
                note?.entities?.apply {
                    compress().let { add(ContentImage(it.toBase64(), it)) }
                }
            }
        }.completeOn(Workers.ui())
                .doOnComplete {
                    editAdapter.itemInserted(note?.entities?.size!! - 1)
                    tagsToEnd()
                    focus()
                }.execute()
    }

    private fun tagsToEnd() {
        note?.entities?.first { it is ContentTags }?.offset(note?.entities!!).let {
            note?.entities?.apply { add(removeAt(it!!)) }
            editAdapter.itemRemoved(it!!)
            editAdapter.itemInserted(note?.entities?.size!! - 1)
        }
    }

    private fun notifyAboutInsertion(action: () -> Unit) {
        worker { action() }
                .doOnComplete {
                    editAdapter.itemInserted(note?.entities?.size!! - 1)
                    tagsToEnd()
                    focus()
                }.completeOn(Workers.ui()).execute()
    }

    private fun focus() {
        editAdapter.focusOn(note?.entities?.size!! - 1)
    }

    override fun onEmptyTag() {
        view?.showSnack(Messages.EMPTY_TAG)
    }

    override fun onClearList(offset: Int) {
        note?.entities?.removeAt(offset)
        editAdapter.itemRemoved(offset)
    }

    override fun buildPrioritiesPicker(): SingleSubscriber<ArrayList<Int>> {
        return singleWorker {
            val items = arrayListOf<Int>()
            val position = (note?.entities?.first { it is ContentPriority } as ContentPriority).let {
                when (it.priority) {
                    Priorities.LOW -> 0
                    Priorities.MEDIUM -> 1
                    Priorities.HIGH -> 2
                    Priorities.URGENT -> 3
                    else -> 0
                }
            }
            val inactive = example.testapp.na.R.drawable.add_note_inactive_priority_bg
            val drawables = arrayListOf(
                    Pair(inactive, example.testapp.na.R.drawable.add_note_low_priority_bg),
                    Pair(inactive, example.testapp.na.R.drawable.add_done_medium_priority_bg),
                    Pair(inactive, example.testapp.na.R.drawable.add_note_high_priority_bg),
                    Pair(inactive, example.testapp.na.R.drawable.add_note_urgent_priority_bg))
            (0 until 4).forEach { items.add(inactive) }
            items[position] = drawables[position].second
            items
        }.disposeOnComplete(true)
    }

    override fun onPriorityPicked(priority: Int) {
        worker { (note?.entities?.first { it is ContentPriority } as ContentPriority).priority = priority }
                .doOnComplete { editAdapter.itemUpdated(0) }
                .completeOn(Workers.ui())
                .execute()
    }

    override fun onTimeSet(): TimePickerDialog.OnTimeSetListener {
        return TimePickerDialog.OnTimeSetListener { _, hourOfDay, minute, _ ->
            sharedCalendar.setTime(hourOfDay, minute)
        }
    }

    override fun onDateSet(): DatePickerDialog.OnDateSetListener {
        return DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
            sharedCalendar.setDate(year, monthOfYear, dayOfMonth)
        }
    }

    override fun changeRemindType(which: Int, caller: View): Task {
        return worker {
            val nextType = when (which) {
                example.testapp.na.R.id.typeNotification ->
                    Types.NOTE_TYPE_NOTIFICATION
                example.testapp.na.R.id.typeAlarm ->
                    Types.NOTE_TYPE_ALARM
                else -> Types.NOTE_TYPE_NO_SIGNAL
            }
            if (note?.params?.type != nextType) {
                note?.params?.type = nextType
                if (nextType != Types.NOTE_TYPE_NO_SIGNAL) {
                    launch(UI) { onTypeChanged(caller) }
                }
            }
        }
    }

    override fun onTypeChanged(caller: View) {
        view?.showSnackOnChild(caller, when (note?.params?.type) {
            Types.NOTE_TYPE_NOTIFICATION -> Messages.REMIND_NOTIFICATION
            Types.NOTE_TYPE_ALARM -> Messages.REMIND_ALARM
            else -> Messages.REMIND_NOTIFICATION
        })
    }

    override fun onDismissRemind(onEnd: () -> Unit) {
        worker {
            clearReminderSettings()
            scheduleHelper.cancel(note!!, {})
        }.doOnComplete {
            onEnd()
            view?.apply {
                changeNotificationState(false)
                showSnack(Messages.REMIND_NO)
            }
        }.completeOn(Workers.ui())
                .execute()
    }

    private fun clearReminderSettings() {
        note?.params?.apply {
            type = Types.NOTE_TYPE_NO_SIGNAL
            timeMillis = 0
            weekDays = RealmList()
            repeatingInterval = 0L
            repeatingPeriod = 0L
            repeatingIntervalPair = Pair(0, "")
            time = "0"
            date = "0"
        }
        sharedCalendar.setMillis(0)
    }

    override fun lockNote() {
        executeMainCommand(worker {
            note?.params?.locked = !note?.params?.locked!!
        }, {
            view?.apply {
                onNoteLocked()
                showSnack(note?.params?.locked
                        ?.let {
                            if (it) Messages.NOTE_LOCKED
                            else Messages.NOTE_UNLOCKED
                        }!!)
            }
        })
    }

    private fun onNoteLocked() {
        view?.changeLockState(note?.params?.locked!!)
    }

    override fun pinNote() {
        executeMainCommand(worker {
            note?.params?.pinned = !note?.params?.pinned!!
        }, {
            view?.apply {
                onNotePinned()
                showSnack(note?.params?.pinned
                        ?.let {
                            if (it) Messages.NOTE_PINNED
                            else Messages.NOTE_UNPINNED
                        }!!)
            }
        })
    }

    private fun onNotePinned() {
        view?.changePinState(note?.params?.pinned!!)
    }

    override fun archiveNote() {
        worker {
            scheduleHelper.cancel(note!!, {
                executeMainCommand(worker {
                    note?.archived = true
                }, {
                    backToNotesList()
                })
            })
        }.execute()
    }

    private fun executeMainCommand(task: Task, onEnd: () -> Unit) {
        task.completeOn(Workers.ui()).doOnComplete(onEnd).execute()
    }

    override fun saveTimeDateSettings() {
        singleWorker {
            sharedCalendar.let {
                if (it.getMillis() == 0L) {
                    clearReminderSettings()
                    return@singleWorker false
                } else {
                    note?.params?.apply {
                        timeMillis = it.getMillis()
                        time = it.getStringTime()
                        date = it.getStringDate()
                        if (type == Types.NOTE_TYPE_NO_SIGNAL) {
                            type = Types.NOTE_TYPE_NOTIFICATION
                        }
                    }
                    return@singleWorker true
                }
            }
        }.subscribe(onSaveSettings(), Workers.ui()).execute()
    }

    private fun onSaveSettings() = object : Subscription<Boolean> {
        override fun onComplete(result: Boolean) {
            view?.changeNotificationState(result)
            if (result) {
                view?.showSnack(
                                if (note?.params?.type == Types.NOTE_TYPE_ALARM)
                                    Messages.BE_ALARMED
                                else Messages.BE_REMINDED)
            }
        }
    }

    override fun checkoutReminderType(onCheckout: (which: Int) -> Unit) {
        onCheckout(note?.params?.type!!)
    }

    override fun backToNotesList() {
        saveNote {
            if (config.editMode) {
                updatesHelper.onNoteUpdated(note?.id!!)
            } else {
                updatesHelper.onNoteInserted(note?.id!!)
            }
        }
        router.backToMain()
    }

    private fun saveNote(onSave: () -> Unit) {
        scheduleHelper.schedule(note!!, false, {
            if (config.editMode) {
                interactor.updateNote(note!!, {
                    scheduleHelper.startOperation(note!!.id)
                    onSave()
                }).execute()
            } else {
                interactor.addNote(note!!, {
                    scheduleHelper.startOperation(note!!.id)
                    onSave()
                }).execute()
            }
        })
    }

    override fun removeItem(position: Int) {
        worker { note?.entities?.removeAt(position) }
                .doOnComplete {
                    editAdapter.itemRemoved(position)
                    itemTouchHelperExtension.closeOpened()
                }
                .completeOn(Workers.ui())
                .execute()
    }

    override fun onWeekdaysUpdate(weekdays: ArrayList<Int>) {
        worker {
            note?.params?.apply {
                if (weekdays.isNotEmpty()) {
                    repeatingInterval = 0L
                    repeatingPeriod = 0L
                }
                weekDays?.clear()
                weekDays?.addAll(weekdays)
            }
        }.execute()
    }

    override fun loadWeekdays(): SingleSubscriber<ArrayList<Int>> {
        return singleWorker {
            return@singleWorker arrayListOf<Int>().apply { addAll(note?.params?.weekDays!!) }
        }
    }

    override fun onIntervalUpdate(interval: Long, period: Long) {
        worker {
            note?.params?.apply {
                if (interval != 0L) {
                    weekDays?.clear()
                }
                repeatingInterval = interval
                repeatingPeriod = period
            }
        }.execute()
    }

    override fun loadInterval(): SingleSubscriber<Pair<Long, Long>> {
        return singleWorker {
            return@singleWorker note?.params!!.let { Pair(it.repeatingInterval, it.repeatingPeriod) }
        }
    }
}