package example.testapp.na.core.managers.app

interface UpdatesHelper {

    fun addUpdatesHandler(onUpdate: (noteId: Long) -> Unit)

    fun addInsertionHandler(onInsert: (noteId: Long) -> Unit)

    fun onNoteUpdated(noteId: Long)

    fun onNoteInserted(noteId: Long)
}