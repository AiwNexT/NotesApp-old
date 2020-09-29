package example.testapp.na.screens.edit.interactor

import android.os.Bundle
import example.testapp.na.vipercore.interactor.Interactor
import example.testapp.na.data.entities.notes.Note
import example.testapp.na.screens.edit.dto.ConfigEdit
import rc.extensions.handlers.single.SingleSubscriber
import rc.extensions.handlers.single.Task
import rc.extensions.streaming.Subscription

interface IEditInteractor: Interactor {

    fun loadExtras(extras: Bundle?)

    fun loadConfig(onLoaded: () -> Unit): SingleSubscriber<ConfigEdit>

    fun loadNote(noteId: Long): SingleSubscriber<Note>

    fun addNote(note: Note, onSaved: () -> Unit): Task

    fun updateNote(note: Note, onSaved: () -> Unit): Task

    fun createNoteId(subscription: Subscription<Long>): SingleSubscriber<Long>
}