package example.testapp.na.data.entities.notes

import rc.extensions.handlers.single.SingleSubscriber
import rc.extensions.handlers.single.Task
import rc.extensions.scope.RCScope

interface NotesRepo: RCScope {

    fun getNotes(queryArchived: Boolean): SingleSubscriber<ArrayList<Note>>

    fun getNoteById(id: Long): SingleSubscriber<Note>

    fun getNoteByIdOnUi(id: Long): SingleSubscriber<Note>

    fun insertNotes(notes: ArrayList<Note>): Task

    fun insertNote(note: Note): Task

    fun isNotesEmpty(): SingleSubscriber<Boolean>

    fun updateNote(note: Note): Task

    fun createNoteId(): SingleSubscriber<Long>

    fun deleteNote(id: Long): Task
}