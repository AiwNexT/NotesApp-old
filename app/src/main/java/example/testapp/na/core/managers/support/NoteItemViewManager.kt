package example.testapp.na.core.managers.support

import android.support.v7.widget.RecyclerView
import android.view.View
import example.testapp.na.core.managers.app.AnimationsHelper
import example.testapp.na.screens.main.view.INoteContentAdapter
import example.testapp.na.vipercore.view.ActivityView
import rc.extensions.scope.ui
import javax.inject.Inject

class NoteItemViewManager : INoteItemViewHelper {

    @Inject
    lateinit var animations: AnimationsHelper

    override fun create(v: ActivityView) {
        v.supportComponent().inject(this)
    }

    override fun update(note: View, noteView: View, additionalHeight: Int, adapter: INoteContentAdapter) {
        processContent(note, noteView as RecyclerView, additionalHeight, adapter)
    }

    private fun processContent(note: View, noteView: RecyclerView, additionalHeight: Int, adapter: INoteContentAdapter) {
        ui {
            var measured = measureViews(noteView, adapter) + additionalHeight + 40
            measured = Math.max(measured, 156)
            if (measured != note.height) {
                note.height.apply {
                    if (this > measured) {
                        animations.hideObjectByHeight(note, measured, {}, false)
                    } else {
                        animations.showObjectByHeight(note, measured, {}, false)
                    }
                }
            }
        }.execute()
    }

    private fun measureViews(noteView: RecyclerView, adapter: INoteContentAdapter): Int {
        var value = 0
        noteView.measure(View.MeasureSpec.makeMeasureSpec(noteView.width, View.MeasureSpec.EXACTLY),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED))
        value += noteView.measuredHeight
        return value
    }
}