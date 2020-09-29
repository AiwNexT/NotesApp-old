package example.testapp.na.screens.main.view

import example.testapp.na.data.entities.notes.Note
import example.testapp.na.vipercore.view.AdapterView

interface INoteItemsAdapter: AdapterView {

    fun updateItemContent()

    fun updateItem(note: Note)
}