package example.testapp.na.tools.custom.drawers.edit

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import example.testapp.na.data.entities.notes.notescontent.ContentNote
import example.testapp.na.data.entities.notes.notescontent.NoteContent
import example.testapp.na.screens.edit.view.IEditView
import kotlinx.android.synthetic.main.edit_item_note.view.*

class NoteDrawer: EditDrawer {

    private lateinit var content: ContentNote
    private lateinit var v: IEditView

    override fun <C : NoteContent> attach(v: IEditView, data: C): NoteDrawer {
        this.content = data as ContentNote
        this.v = v
        return this
    }

    override fun draw(parent: View) {
        parent.editNoteText.apply {
            setText(content.note)
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    content.note = s?.toString()!!
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }
}