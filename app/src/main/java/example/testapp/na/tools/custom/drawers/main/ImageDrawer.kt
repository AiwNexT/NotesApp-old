package example.testapp.na.tools.custom.drawers.main

import android.view.View
import example.testapp.na.data.entities.notes.notescontent.ContentImage
import example.testapp.na.data.entities.notes.notescontent.NoteContent
import example.testapp.na.screens.main.view.IMainView
import kotlinx.android.synthetic.main.main_item_image.view.*

class ImageDrawer: MainDrawer {

    private lateinit var content: ContentImage
    private lateinit var v: IMainView

    override fun <C : NoteContent> attach(v: IMainView, data: C): MainDrawer {
        content = data as ContentImage
        this.v = v
        return this
    }

    override fun draw(parent: View) {
        parent.mainNoteImage.setImageBitmap(content.image)
    }
}