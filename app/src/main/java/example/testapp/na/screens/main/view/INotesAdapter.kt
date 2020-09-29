package example.testapp.na.screens.main.view

import example.testapp.na.data.entities.notes.Note
import example.testapp.na.vipercore.view.AdapterView

interface INotesAdapter: AdapterView {

    fun itemRemoved(position: Int)

    fun itemMoved(from: Int, to: Int)

    fun itemUpdatedWithContentChange(position: Int, note: Note)
}