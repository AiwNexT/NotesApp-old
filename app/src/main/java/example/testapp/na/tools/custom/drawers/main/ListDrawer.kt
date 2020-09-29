package example.testapp.na.tools.custom.drawers.main

import android.app.Activity
import android.support.v7.widget.LinearLayoutManager
import android.view.View
import example.testapp.na.data.entities.notes.notescontent.ContentList
import example.testapp.na.data.entities.notes.notescontent.NoteContent
import example.testapp.na.screens.main.view.IMainList
import example.testapp.na.screens.main.view.IMainView
import example.testapp.na.screens.main.view.MainListAdapter
import kotlinx.android.synthetic.main.main_item_list.view.*

class ListDrawer: MainDrawer {

    private lateinit var content: ContentList
    private lateinit var v: IMainView
    private lateinit var adapter: IMainList

    override fun <C : NoteContent> attach(v: IMainView, data: C): MainDrawer {
        content = data as ContentList
        this.v = v
        return this
    }

    override fun draw(parent: View) {
        parent.mainNoteList.apply {
            setHasFixedSize(true)
            layoutManager = LinearLayoutManager(v as Activity,
                    LinearLayoutManager.VERTICAL, false)
            this@ListDrawer.adapter = MainListAdapter(content)
            adapter = this@ListDrawer.adapter as MainListAdapter
            adapter.notifyItemRangeInserted(0, content.items.size)
        }
    }
}