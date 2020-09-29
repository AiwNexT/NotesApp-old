package example.testapp.na.screens.main.view

import example.testapp.na.data.entities.notes.Note
import example.testapp.na.vipercore.view.AdapterView

interface INoteContentAdapter: AdapterView {

    fun detectTypes()

    fun getCount(): Int

    fun updateNote(note: Note)
}