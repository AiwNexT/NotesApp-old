package example.testapp.na.data.entities.notes

import com.vicpin.krealmextensions.*
import example.testapp.na.tools.extensions.restore
import example.testapp.na.tools.extensions.updateAndSave
import rc.extensions.handlers.single.SingleSubscriber
import rc.extensions.handlers.single.Task
import rc.extensions.scope.*
import javax.inject.Inject

class NotesRepository @Inject internal constructor() : NotesRepo {

    override fun getNotes(queryArchived: Boolean): SingleSubscriber<ArrayList<Note>> {
        return singleWorker {
            return@singleWorker arrayListOf<Note>().apply {
                addAll(Note().let { if (queryArchived) it.queryAll() else
                    it.query { notEqualTo("archived", true) } }.restore())
            }
        }
    }

    override fun getNoteById(id: Long): SingleSubscriber<Note> {
        return singleWorker {
            return@singleWorker Note().queryFirst { equalTo("id", id) }?.restore()!!
        }
    }

    override fun getNoteByIdOnUi(id: Long): SingleSubscriber<Note> {
        return singleUi {
            return@singleUi Note().queryFirst { equalTo("id", id) }?.restore()!!
        }
    }

    override fun insertNotes(notes: ArrayList<Note>): Task {
        return merged(8) { notes.updateAndSave() }
    }

    override fun insertNote(note: Note): Task {
        return worker { note.updateAndSave() }
    }

    override fun isNotesEmpty(): SingleSubscriber<Boolean> {
        return singleMerged(8) { Note().queryAll().isEmpty() }
    }

    override fun updateNote(note: Note): Task {
        return worker { note.updateAndSave() }
    }

    override fun createNoteId(): SingleSubscriber<Long> {
        return singleDedicated {
            Note().queryAll().let { it.isNotEmpty().let { n -> if (n) it.maxBy { it.id }?.id!! + 1 else 0 } }
        }
    }

    override fun deleteNote(id: Long): Task {
        return worker {
            Note().delete { equalTo("id", id) }
        }
    }
}