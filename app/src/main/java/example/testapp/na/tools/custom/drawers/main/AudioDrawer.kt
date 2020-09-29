package example.testapp.na.tools.custom.drawers.main

import android.view.View
import example.testapp.na.data.entities.notes.notescontent.ContentAudio
import example.testapp.na.data.entities.notes.notescontent.NoteContent
import example.testapp.na.screens.main.view.IMainView

class AudioDrawer: MainDrawer {

    private lateinit var content: ContentAudio
    private lateinit var v: IMainView

    override fun <C : NoteContent> attach(v: IMainView, data: C): MainDrawer {
        content = data as ContentAudio
        this.v = v
        return this
    }

    override fun draw(parent: View) {

    }
}