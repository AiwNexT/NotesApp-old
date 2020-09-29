package example.testapp.na.screens.edit.view

import example.testapp.na.vipercore.view.AdapterView
import example.testapp.na.data.entities.notes.Note
import rc.extensions.handlers.single.Task

interface IEditAdapter: AdapterView {

    fun attachParentView(view: IEditView): Task

    fun attachNote(note: Note)

    fun onNoteCreated()

    fun attachLayouts(layouts: ArrayList<Pair<Int, Int>>)

    fun focusOn(offset: Int)

    fun itemUpdatedWithInsertion(offset: Int, childInserted: Int)

    fun itemRemoved(offset: Int)

    fun itemsRangeUpdated(start: Int, amount: Int)

    fun itemMoved(from: Int, to: Int)
}