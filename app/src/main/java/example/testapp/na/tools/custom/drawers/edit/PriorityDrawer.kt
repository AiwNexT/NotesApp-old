package example.testapp.na.tools.custom.drawers.edit

import android.view.View
import butterknife.ButterKnife
import example.testapp.na.R
import example.testapp.na.data.entities.notes.notescontent.ContentPriority
import example.testapp.na.data.entities.notes.notescontent.NoteContent
import example.testapp.na.screens.edit.view.IEditView
import example.testapp.na.tools.credentials.Priorities
import kotlinx.android.synthetic.main.edit_item_priority.view.*

class PriorityDrawer: EditDrawer {

    private lateinit var content: ContentPriority
    private lateinit var v: IEditView

    override fun <C : NoteContent> attach(v: IEditView, data: C): PriorityDrawer {
        this.content = data as ContentPriority
        this.v = v
        inject()
        return this
    }

    override fun draw(parent: View) {
        ButterKnife.bind(this, parent)

        drawText(parent)
        drawRect(parent)

        parent.editPriorityRipple.setOnClickListener {
            openPrioritySheet()
        }
    }

    private fun drawText(parent: View) {
        parent.editPriorityText.text = when(content.priority) {
            Priorities.LOW -> Priorities.LOW_TEXT
            Priorities.MEDIUM -> Priorities.MEDIUM_TEXT
            Priorities.HIGH -> Priorities.HIGH_TEXT
            Priorities.URGENT -> Priorities.URGENT_TEXT
            else -> Priorities.LOW_TEXT
        }
    }

    private fun openPrioritySheet() {
        v.onPriorityClicked()
    }

    private fun drawRect(parent: View) {
        parent.editPriorityText.background =
                parent.context.resources.getDrawable(when(content.priority) {
                    Priorities.LOW -> R.drawable.add_note_low_priority_bg
                    Priorities.MEDIUM -> R.drawable.add_done_medium_priority_bg
                    Priorities.HIGH -> R.drawable.add_note_high_priority_bg
                    Priorities.URGENT -> R.drawable.add_note_urgent_priority_bg
                    else -> R.drawable.add_note_low_priority_bg
                })
    }
}