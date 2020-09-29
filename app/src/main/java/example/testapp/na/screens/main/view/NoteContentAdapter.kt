package example.testapp.na.screens.main.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import example.testapp.na.R
import example.testapp.na.data.entities.notes.Note
import example.testapp.na.data.entities.notes.notescontent.*
import example.testapp.na.tools.credentials.Types
import example.testapp.na.tools.custom.drawers.main.*
import rc.extensions.scope.RCScope

class NoteContentAdapter(private val view: IMainView,
                         private var note: Note) : INoteContentAdapter, RCScope,
        RecyclerView.Adapter<NoteContentAdapter.NoteContentHolder>() {

    private var entities: ArrayList<Pair<Int, MainDrawer>> = arrayListOf()

    init {
        detectTypes()
    }

    private val layouts = arrayListOf(
            Pair(Types.LAYOUT_AUDIO, R.layout.main_item_audio),
            Pair(Types.LAYOUT_NOTE, R.layout.main_item_note),
            Pair(Types.LAYOUT_LIST, R.layout.main_item_list),
            Pair(Types.LAYOUT_IMAGE, R.layout.main_item_image))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteContentHolder {
        return NoteContentHolder(LayoutInflater.from(parent.context)
                .inflate(layouts.first { it.first == viewType }.second, parent, false))
    }

    override fun getItemCount(): Int {
        return note.entities
                .filter { (it !is ContentPriority) and (it !is ContentName) and (it !is ContentTags) }
                .size.let { if (it > getMaxSize()) getMaxSize() else it }
    }

    override fun onBindViewHolder(holder: NoteContentHolder, position: Int) {
        entities[holder.adapterPosition].second.attach(view, note.entities
                .filter { (it !is ContentPriority) and (it !is ContentName) and (it !is ContentTags) }[holder.adapterPosition])
                .draw(holder.itemView)
    }

    override fun getItemViewType(position: Int): Int {
        return entities[position].first
    }

    override fun detectTypes() {
        note.entities
                .filter { (it !is ContentPriority) and (it !is ContentName) and (it !is ContentTags) }
                .forEach {
                    entities.add(when (it) {
                        is ContentNote -> Pair(Types.LAYOUT_NOTE, NoteDrawer())
                        is ContentAudio -> Pair(Types.LAYOUT_AUDIO, AudioDrawer())
                        is ContentList -> Pair(Types.LAYOUT_LIST, ListDrawer())
                        is ContentImage -> Pair(Types.LAYOUT_IMAGE, ImageDrawer())
                        else -> Pair(Types.LAYOUT_NOTE, NoteDrawer())
                    })
                }
    }

    private fun getMaxSize(): Int {
        return note.entities.filter { (it !is ContentPriority) and (it !is ContentName) and (it !is ContentTags) }.size.apply {
            return when {
                this == 1 -> this
                this == 2 -> if (note.entities[2] is ContentImage) {
                    if (note.entities[3] is ContentImage) { this - 1 } else { this }
                } else { this }
                this >= 3 -> when {
                    note.entities[2] is ContentImage -> if (note.entities[3] is ContentImage) { 1 } else if (note.entities[4] is ContentImage) { 2 } else { 3 }
                    note.entities[3] is ContentImage -> if (note.entities[4] is ContentImage) { 2 } else { 3 }
                    note.entities[4] is ContentImage -> 3
                    else -> { 3 }
                }
                else -> 0
            }
        }
    }

    override fun getCount(): Int {
        return itemCount
    }

    override fun updateNote(note: Note) {
        this.note = note
        entities.clear()
        detectTypes()
    }

    inner class NoteContentHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)

    override fun dataChange() = notifyDataSetChanged()

    override fun itemInserted(position: Int) = notifyItemInserted(position)

    override fun itemsRangeInserted(start: Int, amount: Int) = notifyItemRangeInserted(start, amount)

    override fun itemUpdated(position: Int) = notifyItemChanged(position)
}