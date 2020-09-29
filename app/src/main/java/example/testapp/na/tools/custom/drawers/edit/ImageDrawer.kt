package example.testapp.na.tools.custom.drawers.edit

import android.view.View
import example.testapp.na.data.entities.notes.notescontent.ContentImage
import example.testapp.na.data.entities.notes.notescontent.NoteContent
import example.testapp.na.screens.edit.view.IEditView
import kotlinx.android.synthetic.main.edit_item_image.view.*

class ImageDrawer: EditDrawer {

    private lateinit var v: IEditView
    private lateinit var content: ContentImage

    override fun <C : NoteContent> attach(v: IEditView, data: C): ImageDrawer {
        this.content = data as ContentImage
        this.v = v
        return this
    }

    override fun draw(parent: View) {
        parent.editNoteImage.setImageBitmap(content.image)
    }
}