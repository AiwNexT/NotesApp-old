package example.testapp.na.tools.custom.drawers.main

import android.view.View
import example.testapp.na.data.entities.notes.notescontent.NoteContent
import example.testapp.na.screens.main.view.IMainView

interface MainDrawer {

    fun<C: NoteContent> attach(v: IMainView, data: C): MainDrawer

    fun draw(parent: View)

    fun inject() {}
}