package example.testapp.na.screens.main.interactor

import example.testapp.na.vipercore.interactor.Interactor
import example.testapp.na.data.entities.notes.Note
import example.testapp.na.screens.main.dto.ConfigMain
import rc.extensions.handlers.single.SingleSubscriber
import rc.extensions.handlers.single.Task

interface IMainInteractor: Interactor {

    fun loadAllNotes(onLoaded: () -> Unit): SingleSubscriber<ArrayList<Note>>

    fun loadNoteById(id: Long): SingleSubscriber<Note>

    fun getConfig(onLoaded: () -> Unit): SingleSubscriber<ConfigMain>

    fun updateNote(note: Note): Task

    fun insertNote(note: Note): Task

    fun deleteNote(id: Long): Task
}