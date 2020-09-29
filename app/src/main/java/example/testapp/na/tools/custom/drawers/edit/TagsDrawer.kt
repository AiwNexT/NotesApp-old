package example.testapp.na.tools.custom.drawers.edit

import android.app.Activity
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import example.testapp.na.data.entities.notes.notescontent.ContentTags
import example.testapp.na.data.entities.notes.notescontent.NoteContent
import example.testapp.na.screens.edit.view.IEditAdapter
import example.testapp.na.screens.edit.view.IEditView
import example.testapp.na.screens.edit.view.ITags
import example.testapp.na.screens.edit.view.TagsAdapter
import example.testapp.na.tools.extensions.Delayable
import example.testapp.na.tools.extensions.wait
import kotlinx.android.synthetic.main.edit_item_tags.view.*
import kotlinx.android.synthetic.main.tag_item.view.*
import kotlinx.coroutines.experimental.launch

class TagsDrawer : EditDrawer, Delayable {

    private lateinit var v: IEditView
    private lateinit var content: ContentTags

    private lateinit var parentAdapter: IEditAdapter

    private lateinit var adapter: ITags
    private lateinit var parent: View

    private var offset = 0

    override fun <C : NoteContent> attach(v: IEditView, data: C): TagsDrawer {
        this.v = v
        this.content = data as ContentTags
        return this
    }

    fun attachParentAdapter(adapter: IEditAdapter, offset: Int) {
        this.parentAdapter = adapter
        this.offset = offset
    }

    override fun draw(parent: View) {
        this.parent = parent
        parent.editNoteTags.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(v as Activity,
                    LinearLayoutManager.HORIZONTAL, false)
            this@TagsDrawer.adapter = TagsAdapter(offset, content)
            adapter = this@TagsDrawer.adapter as TagsAdapter
            (adapter as ITags).attachParentView(v)
            adapter.notifyItemRangeInserted(0, content.tags.size)
        }
    }

    fun onItemInserted(position: Int) {
        adapter.itemInserted(position)
        focusOn(position)
    }

    private fun focusOn(position: Int) = launch {
        parent.editNoteTags.layoutManager.apply {
            wait {
                scrollToPosition(position)
                wait {
                    findViewByPosition(position).let {
                        it.requestFocus()
                        invokeSoftInput(it)
                    }
                }
            }
        }
    }

    private fun invokeSoftInput(view: View) {
        (v.routingContext()?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager)
                .showSoftInput(view.editNoteTagText, InputMethodManager.SHOW_IMPLICIT)
    }
}