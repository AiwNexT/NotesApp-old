package example.testapp.na.screens.main.view

import example.testapp.na.vipercore.view.ActivityView
import example.testapp.na.core.managers.support.INoteItemViewHelper
import example.testapp.na.data.entities.notes.Note
import example.testapp.na.tools.elements.MutablePair

interface IMainView: ActivityView {

    fun bindNotesView(isGrid: Boolean)

    fun openDrawer()

    fun closeDrawer()

    fun bindDrawer()

    fun bindNotesAdapter(notes: ArrayList<MutablePair<Note, INoteItemViewHelper>>)

    fun showFabMenu()

    fun hideFabMenu()

    fun onAddClicked()

    fun onCloseClicked()

    fun onAddNoteClicked(view: android.view.View)

    fun onControlsClicked(view: android.view.View)

    fun showAsList()

    fun showAsGrid()

    fun onNoteClicked(id: Long)

    fun onArchiveClicked(position: Int)

    fun showUndoRemovalSnack()

    fun showSnack(message: String)

    fun onUndoRemoval()
}