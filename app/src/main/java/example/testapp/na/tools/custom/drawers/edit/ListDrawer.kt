package example.testapp.na.tools.custom.drawers.edit

import android.app.Activity
import android.content.Context
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import android.view.inputmethod.InputMethodManager
import example.testapp.na.R
import example.testapp.na.data.entities.notes.notescontent.ContentList
import example.testapp.na.data.entities.notes.notescontent.NoteContent
import example.testapp.na.screens.edit.view.IEditAdapter
import example.testapp.na.screens.edit.view.IEditView
import example.testapp.na.screens.edit.view.IList
import example.testapp.na.screens.edit.view.ListAdapter
import example.testapp.na.tools.extensions.Delayable
import example.testapp.na.tools.extensions.wait
import kotlinx.android.synthetic.main.edit_item_list.view.*
import kotlinx.android.synthetic.main.edit_list_item.view.*
import kotlinx.coroutines.experimental.launch

class ListDrawer: EditDrawer, Delayable {

    private lateinit var content: ContentList
    private lateinit var v: IEditView

    private lateinit var parentAdapter: IEditAdapter

    private lateinit var adapter: IList
    private lateinit var parent: View

    private var offset: Int = 0

    override fun <C : NoteContent> attach(v: IEditView,  data: C): ListDrawer {
        this.content = data as ContentList
        this.v = v
        return this
    }

    fun attachParentAdapter(adapter: IEditAdapter, offset: Int) {
        this.parentAdapter = adapter
        this.offset = offset
    }

    override fun draw(parent: View) {
        this.parent = parent
        parent.editNoteList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(v as Activity,
                    LinearLayoutManager.VERTICAL, false)
            this@ListDrawer.adapter = ListAdapter(R.layout.edit_list_item, offset, content)
            adapter = this@ListDrawer.adapter as ListAdapter
            (adapter as IList).attachParentView(v)
            adapter.notifyItemRangeInserted(0, content.items.size)
        }
    }

    fun onItemInserted(position: Int) {
        adapter.itemInserted(position)
        focusOn(position)
    }

    private fun focusOn(position: Int) = launch {
        parent.editNoteList.layoutManager.apply {
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
                .showSoftInput(view.editListItemText, InputMethodManager.SHOW_IMPLICIT)
    }
}