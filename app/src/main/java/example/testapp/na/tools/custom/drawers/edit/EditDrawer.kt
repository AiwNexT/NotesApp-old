package example.testapp.na.tools.custom.drawers.edit

import android.view.View
import example.testapp.na.data.entities.notes.notescontent.NoteContent
import example.testapp.na.screens.edit.view.IEditView

interface EditDrawer {

    fun<C: NoteContent> attach(v: IEditView, data: C): EditDrawer

    fun draw(parent: View)

    fun inject() {}
}