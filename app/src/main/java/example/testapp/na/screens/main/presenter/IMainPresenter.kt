package example.testapp.na.screens.main.presenter

import android.support.v7.widget.RecyclerView
import example.testapp.na.data.entities.notes.Note
import example.testapp.na.vipercore.presenter.Presenter
import example.testapp.na.screens.main.interactor.IMainInteractor
import example.testapp.na.screens.main.router.IMainRouter
import example.testapp.na.screens.main.view.IMainView
import example.testapp.na.screens.main.view.INotesAdapter

interface IMainPresenter<V: IMainView, I: IMainInteractor, R: IMainRouter>: Presenter<V, I, R> {

    fun loadNotes()

    fun loadConfig()

    fun attachNotesAdapter(notesAdapter: INotesAdapter)

    fun onAddItemClicked(currentMenuState: Boolean)

    fun onAddNoteItemClicked(which: Int)

    fun onControlItemClicked(which: Int)

    fun onNoteInserted(id: Long)

    fun onNoteUpdated(id: Long)

    fun onNoteClicked(id: Long)

    fun onListInit(list: RecyclerView)

    fun onArchiveClicked(position: Int)

    fun undoRemoval()

    fun bindForUpdates()

    fun removeNote(position: Int)

    fun cancelNoteNotifications(note: Note)

    fun rescheduleNotifications(note: Note, onSchedule: () -> Unit)
}