package example.testapp.na.core.managers.app

import javax.inject.Inject

class UpdatesSubscriber @Inject constructor(): UpdatesHelper {

    private var onUpdate: ((noteId: Long) -> Unit)? = null
    private var onInsert: ((noteId: Long) -> Unit)? = null

    override fun addUpdatesHandler(onUpdate: (noteId: Long) -> Unit) {
        this.onUpdate = onUpdate
    }

    override fun addInsertionHandler(onInsert: (noteId: Long) -> Unit) {
        this.onInsert = onInsert
    }

    override fun onNoteUpdated(noteId: Long) {
        onUpdate?.invoke(noteId)
    }

    override fun onNoteInserted(noteId: Long) {
        onInsert?.invoke(noteId)
    }
}