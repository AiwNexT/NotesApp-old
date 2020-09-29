package example.testapp.na.tools.custom.drawers.edit

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import example.testapp.na.data.entities.notes.notescontent.ContentName
import example.testapp.na.data.entities.notes.notescontent.NoteContent
import example.testapp.na.screens.edit.view.IEditView
import kotlinx.android.synthetic.main.edit_item_name.view.*

class NameDrawer: EditDrawer {

    private lateinit var content: ContentName
    private lateinit var v: IEditView

    override fun <C : NoteContent> attach(v: IEditView, data: C): NameDrawer {
        this.content = data as ContentName
        this.v = v
        return this
    }

    override fun draw(parent: View) {
        parent.editNoteName.apply {
            setText(content.name)
            addTextChangedListener(object : TextWatcher {
                override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
                override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                    content.name = s?.toString()!!
                }
                override fun afterTextChanged(s: Editable?) {}
            })
        }
    }
}