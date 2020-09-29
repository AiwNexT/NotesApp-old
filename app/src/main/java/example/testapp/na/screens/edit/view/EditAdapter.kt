package example.testapp.na.screens.edit.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.loopeer.itemtouchhelperextension.Extension
import example.testapp.na.R
import example.testapp.na.data.entities.notes.Note
import example.testapp.na.data.entities.notes.notescontent.*
import example.testapp.na.tools.credentials.Types
import example.testapp.na.tools.custom.drawers.edit.*
import example.testapp.na.tools.extensions.swap
import kotlinx.android.synthetic.main.background_action_view_edit.view.*
import rc.extensions.handlers.single.Task
import rc.extensions.scope.RCScope
import rc.extensions.scope.worker

class EditAdapter(val main: IEditView) :
        IEditAdapter, RecyclerView.Adapter<EditAdapter.EditHolder>(), RCScope {

    private var note: Note? = null

    private lateinit var parentView: IEditView

    private val entities: ArrayList<Pair<Int, EditDrawer>> = arrayListOf()
    private var layouts: ArrayList<Pair<Int, Int>> = arrayListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): EditHolder {
        return EditHolder((LayoutInflater.from(parent.context)
                .inflate(layouts.first { it.first == viewType }.second, parent, false)))
    }

    override fun getItemCount(): Int {
        return note?.entities?.size ?: 0
    }

    override fun attachNote(note: Note) {
        this.note = note
    }

    override fun onBindViewHolder(holder: EditHolder, position: Int) {
        entities[position].second.attach(main, note?.entities!![position])
        entities[position].apply {
            if (first == Types.LAYOUT_LIST) {
                (second as ListDrawer).attachParentAdapter(this@EditAdapter, holder.adapterPosition)
            } else if (first == Types.LAYOUT_TAGS) {
                (second as TagsDrawer).attachParentAdapter(this@EditAdapter, holder.adapterPosition)
            }
        }
        entities[position].second.draw(holder.itemView)
        holder.itemView.apply {
            removeNote?.setOnClickListener { parentView.onItemRemoveClicked(holder.adapterPosition) }
        }
    }

    override fun getItemViewType(position: Int): Int {
        return entities[position].first
    }

    override fun attachLayouts(layouts: ArrayList<Pair<Int, Int>>) {
        this.layouts = layouts
    }

    inner class EditHolder(itemView: View?) : RecyclerView.ViewHolder(itemView), Extension {
        var removeNote: View? = null
        init {
            removeNote = itemView?.findViewById(R.id.removeNote)
        }

        override fun getActionWidth(): Float {
            return removeNote?.width?.toFloat() ?: 0f
        }
    }

    override fun attachParentView(view: IEditView): Task {
        this.parentView = view
        return worker { detectTypes() }
    }

    override fun onNoteCreated() {
        detectTypes()
    }

    override fun focusOn(offset: Int) {
        parentView.getListManager().apply {
            scrollToPosition(offset)
        }
    }

    private fun detectTypes() {
        note?.entities?.forEach {
            entities.add(getType(it))
        }
    }

    private fun updateTypes() {
        if (note?.entities?.size!! > entities.size) {
            entities.add(getType(note?.entities!![note?.entities!!.size - 1]))
        }
    }

    private fun getType(type: NoteContent): Pair<Int, EditDrawer> {
        return when (type) {
            is ContentName -> Pair(Types.LAYOUT_NAME, NameDrawer())
            is ContentNote -> Pair(Types.LAYOUT_NOTE, NoteDrawer())
            is ContentImage -> Pair(Types.LAYOUT_IMAGE, ImageDrawer())
            is ContentPriority -> Pair(Types.LAYOUT_PRIORITY, PriorityDrawer())
            is ContentAudio -> Pair(Types.LAYOUT_AUDIO, AudioDrawer())
            is ContentList -> Pair(Types.LAYOUT_LIST, ListDrawer())
            is ContentTags -> Pair(Types.LAYOUT_TAGS, TagsDrawer())
            else -> Pair(Types.LAYOUT_NOTE, NoteDrawer())
        }
    }

    override fun dataChange() {
        updateTypes()
        notifyDataSetChanged()
    }

    override fun itemInserted(position: Int) {
        updateTypes()
        notifyItemInserted(position)
    }

    override fun itemUpdatedWithInsertion(offset: Int, childInserted: Int) {
        if (entities[offset].second is ListDrawer) { (entities[offset].second as ListDrawer).onItemInserted(childInserted) }
        else if (entities[offset].second is TagsDrawer) { (entities[offset].second as TagsDrawer).onItemInserted(childInserted) }
    }

    override fun itemsRangeInserted(start: Int, amount: Int) = notifyItemRangeInserted(start, amount)

    override fun itemUpdated(position: Int) = notifyItemChanged(position)

    override fun itemRemoved(offset: Int) {
        entities.removeAt(offset)
        notifyItemRemoved(offset)
    }

    override fun itemsRangeUpdated(start: Int, amount: Int) = notifyItemRangeChanged(start, amount)

    override fun itemMoved(from: Int, to: Int) {
        entities.swap(from, to)
        notifyItemMoved(from, to)
    }
}