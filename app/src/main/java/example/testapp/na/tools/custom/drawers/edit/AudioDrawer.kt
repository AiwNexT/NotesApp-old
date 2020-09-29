package example.testapp.na.tools.custom.drawers.edit

import android.graphics.PorterDuff
import android.support.v7.widget.AppCompatSeekBar
import android.view.View
import example.testapp.na.R
import example.testapp.na.data.entities.notes.notescontent.ContentAudio
import example.testapp.na.data.entities.notes.notescontent.NoteContent
import example.testapp.na.screens.edit.view.IEditView
import kotlinx.android.synthetic.main.edit_item_audio.view.*

class AudioDrawer: EditDrawer {

    private lateinit var content: ContentAudio
    private lateinit var v: IEditView

    override fun <C : NoteContent> attach(v: IEditView, data: C): AudioDrawer {
        this.content = data as ContentAudio
        this.v = v
        return this
    }

    override fun draw(parent: View) {
        colorSeekbar(parent.audioSeekBar)
    }

    private fun colorSeekbar(seekBar: AppCompatSeekBar) {
        seekBar.progressDrawable
                .setColorFilter(v.routingContext()?.resources?.getColor(R.color.colorSeekBar)!!,
                PorterDuff.Mode.SRC_ATOP)
    }
}