package example.testapp.na.screens.main.presenter

import android.os.Bundle
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import example.testapp.na.core.managers.app.ScheduleHelper
import example.testapp.na.core.managers.app.SchedulingUpdatesHelper
import example.testapp.na.core.managers.app.UpdatesHelper
import example.testapp.na.core.managers.support.INoteItemViewHelper
import example.testapp.na.core.managers.support.NoteItemViewManager
import example.testapp.na.data.entities.notes.Note
import example.testapp.na.screens.main.dto.ConfigMain
import example.testapp.na.screens.main.interactor.IMainInteractor
import example.testapp.na.screens.main.router.IMainRouter
import example.testapp.na.screens.main.view.IMainView
import example.testapp.na.screens.main.view.INotesAdapter
import example.testapp.na.tools.credentials.Extras
import example.testapp.na.tools.credentials.Messages
import example.testapp.na.tools.credentials.Types
import example.testapp.na.tools.elements.MutablePair
import example.testapp.na.tools.extensions.swap
import example.testapp.na.vipercore.presenter.BasePresenter
import rc.extensions.handlers.single.Task
import rc.extensions.scope.RCScope
import rc.extensions.scope.merged
import rc.extensions.scope.ui
import rc.extensions.scope.worker
import rc.extensions.streaming.Subscription
import rc.extensions.workers.Workers
import javax.inject.Inject

class MainPresenter<V : IMainView, I : IMainInteractor, R : IMainRouter>
@Inject internal constructor(private val updatesHandler: UpdatesHelper,
                             private val tracker: SchedulingUpdatesHelper,
                             private val scheduleHelper: ScheduleHelper,
                             interactor: I, router: R) :
        BasePresenter<V, I, R>(interactor = interactor, router = router), IMainPresenter<V, I, R>, RCScope {

    private var notesAdapter: INotesAdapter? = null

    private val notes: ArrayList<MutablePair<Note, INoteItemViewHelper>> = arrayListOf()

    private lateinit var itemTouchHelper: ItemTouchHelper

    private var lastRemoved: Pair<Int, MutablePair<Note, INoteItemViewHelper>>? = null

    private var uniqueTargetId: Int? = null

    override fun onAttach(view: V?) {
        super.onAttach(view)
        worker {
            updatesHandler.addUpdatesHandler { onNoteUpdated(it) }
            updatesHandler.addInsertionHandler { onNoteInserted(it) }
        }.execute()
        view?.bindNotesAdapter(notes)
        loadConfig()
        bindForUpdates()
    }

    override fun onListInit(list: RecyclerView) {
        val itemTouchHelperCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {
            override fun onMove(recyclerView: RecyclerView?, viewHolder: RecyclerView.ViewHolder?, target: RecyclerView.ViewHolder?): Boolean {
                notes.swap(viewHolder!!.adapterPosition, target!!.adapterPosition)
                notesAdapter?.itemMoved(viewHolder.adapterPosition, target.adapterPosition)
                return true
            }
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder?, direction: Int) {
                removeNote(viewHolder!!.adapterPosition)
            }
        }
        itemTouchHelper = ItemTouchHelper(itemTouchHelperCallback).apply {
            attachToRecyclerView(list)
        }
    }

    override fun removeNote(position: Int) {
        worker {
            val lastNote = notes.removeAt(position)
            cancelNoteNotifications(lastNote.first!!)
            lastRemoved = Pair(position, lastNote)
            lastNote.first?.id?.apply {
                interactor.deleteNote(this).execute()
            }
        }.doOnComplete {
            notesAdapter?.itemRemoved(position)
            view?.showUndoRemovalSnack()
        }.completeOn(Workers.ui())
                .execute()
    }

    override fun cancelNoteNotifications(note: Note) {
        scheduleHelper.cancel(note, {})
    }

    override fun rescheduleNotifications(note: Note, onSchedule: () -> Unit) {
        scheduleHelper.schedule(note, false, onSchedule)
    }

    override fun loadConfig() {
        interactor.getConfig({
            loadNotes()
        }).completeOn(Workers.ui())
                .subscribe(object : Subscription<ConfigMain> {
                    override fun onComplete(result: ConfigMain) {
                        result.apply {
                            if (viewType == Types.VIEW_TYPE_GRID) view?.showAsGrid()
                            else view?.showAsList()
                        }
                    }
                }, Workers.ui()).execute()
    }

    override fun loadNotes() {
        interactor.loadAllNotes({}).completeOn(Workers.ui())
                .subscribe(object : Subscription<ArrayList<Note>> {
                    override fun onComplete(result: ArrayList<Note>) {
                        createViewManagers(result)
                                .completeOn(Workers.ui())
                                .doOnComplete {
                                    notesAdapter?.itemsRangeInserted(0, notes.size)
                                }.execute()
                    }
                }, Workers.ui()).execute()
    }

    private fun createViewManagers(loaded: ArrayList<Note>): Task {
        return merged(8) {
            loaded.forEach { notes.add(MutablePair(it, NoteItemViewManager().apply { create(view!!) })) }
        }
    }

    override fun attachNotesAdapter(notesAdapter: INotesAdapter) {
        this.notesAdapter = notesAdapter
    }

    override fun onAddItemClicked(currentMenuState: Boolean) {
        view?.apply {
            currentMenuState.let {
                if (it) {
                    view?.hideFabMenu()
                    onAddNoteItemClicked(Types.TYPE_COMPOSE)
                } else showFabMenu()
            }
        }
    }

    override fun onAddNoteItemClicked(which: Int) {
        if ((which != Types.TYPE_COMPOSE) and
                (which != Types.TYPE_QUICK_NOTE) and
                (which != Types.TYPE_AUDIO)) {
            view?.hideFabMenu()
        }
        when (which) {
            Types.TYPE_NOTE -> router.startEditorActivity(Bundle()
                    .apply { putInt(Extras.EXTRA_NOTE_PARAM, Extras.NOTE_PARAM_QUICK) })
            Types.TYPE_QUICK_NOTE -> router.startEditorActivity(Bundle()
                    .apply { putInt(Extras.EXTRA_NOTE_PARAM, Extras.NOTE_PARAM_QUICK) })
            Types.TYPE_ALARM -> router.startEditorActivity(Bundle()
                    .apply { putInt(Extras.EXTRA_NOTE_PARAM, Extras.NOTE_PARAM_ALARM) })
            Types.TYPE_LIST -> router.startEditorActivity(Bundle()
                    .apply { putInt(Extras.EXTRA_NOTE_PARAM, Extras.NOTE_PARAM_LIST) })
            Types.TYPE_AUDIO -> router.startEditorActivity(Bundle()
                    .apply { putInt(Extras.EXTRA_NOTE_PARAM, Extras.NOTE_PARAM_AUDIO) })
            Types.TYPE_COMPOSE -> router.startEditorActivity(null)
        }
    }

    override fun onNoteClicked(id: Long) {
        router.startEditorActivity(Bundle().apply {
            putLong(Extras.EXTRA_NOTE_ID, id)
            putBoolean(Extras.EDIT_MODE, true)
        })
    }

    override fun onControlItemClicked(which: Int) {
        when (which) {
            Types.TYPE_MENU -> {
                view?.openDrawer()
            }
            Types.TYPE_SEARCH -> {
            }
            Types.TYPE_CHANGE_VIEW -> {
            }
        }
    }

    override fun onNoteInserted(id: Long) {
        var archive = false
        interactor.loadNoteById(id).subscribe(object : Subscription<Note> {
            override fun onComplete(result: Note) {
                if (!result.archived) {
                    notes.add(MutablePair(result, NoteItemViewManager().apply { create(view!!) }))
                } else {
                    archive = true
                }
            }
        }, Workers.default())
                .doOnComplete {
                    if (!archive) {
                        notesAdapter?.itemInserted(notes.size - 1)
                    } else {
                        view?.showSnack(Messages.NOTE_ARCHIVED)
                    }
                }
                .completeOn(Workers.ui())
                .execute()
    }

    override fun undoRemoval() {
        lastRemoved?.let {
            worker {
                notes.add(it.first, it.second)
                rescheduleNotifications(it.second.first!!, {
                    it.second.first?.apply {
                        interactor
                                .insertNote(this)
                                .execute()
                    }
                })
                scheduleHelper.startOperation(it.second.first!!.id)
            }.doOnComplete {
                notesAdapter?.itemInserted(it.first)
            }.completeOn(Workers.ui())
                    .execute()
        }
    }

    override fun onNoteUpdated(id: Long) {
        interactor.loadNoteById(id).subscribe(object : Subscription<Note?> {
            override fun onComplete(result: Note?) {
                var updateAt = 0
                if (result!!.archived) {
                    updateAt = notes.indexOf(notes.first { it.first?.id == id })
                    notes.removeAt(updateAt)
                    notesAdapter?.itemRemoved(updateAt)
                    ui {
                        view?.showSnack(Messages.NOTE_ARCHIVED)
                    }.execute()
                } else {
                    notes.first { it.first?.id == id }.apply {
                        updateAt = notes.indexOf(this)
                        copyNewToNote(this.first!!, result)
                    }
                    ui {
                        notesAdapter?.itemUpdated(updateAt)
                    }.execute()
                }
            }
        }, Workers.default()).execute()
    }

    private fun copyNewToNote(original: Note, new: Note) {
        original.apply {
            entities = new.entities
            params = new.params
            archived = new.archived
            removed = new.removed
        }
    }

    override fun onArchiveClicked(position: Int) {
        notes[position].first?.apply {
            archived = true
            interactor.updateNote(this)
                    .doOnComplete {
                        notesAdapter?.itemUpdated(position)
                    }.completeOn(Workers.ui())
                    .execute()
        }
    }

    override fun bindForUpdates() {
        tracker.startTracking()
        uniqueTargetId = tracker.addTarget {
            updateNote(it)
        }
    }

    private fun updateNote(id: Long) = worker {
        interactor.loadNoteById(id).subscribe(object : Subscription<Note> {
            override fun onComplete(result: Note) {
                notes.firstOrNull { it.first?.id == result.id }?.apply {
                    first = result
                    first?.let { n ->
                        notesAdapter?.itemUpdatedWithContentChange(notes.indexOfFirst { it.first?.id == n.id }, first!!)
                    }
                }
            }
        }, Workers.ui()).execute()
    }.execute()

    override fun onDetach() {
        super.onDetach()
        uniqueTargetId?.let { tracker.removeTarget(it) }
    }
}