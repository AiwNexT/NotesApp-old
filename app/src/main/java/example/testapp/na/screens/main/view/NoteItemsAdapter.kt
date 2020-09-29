package example.testapp.na.screens.main.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import example.testapp.na.R
import example.testapp.na.data.entities.notes.Note
import example.testapp.na.data.entities.notes.notescontent.*
import example.testapp.na.tools.credentials.Types
import kotlinx.android.synthetic.main.note_item_type.view.*
import rc.extensions.scope.RCScope
import rc.extensions.scope.singleDedicated
import rc.extensions.scope.worker
import rc.extensions.streaming.Subscription
import rc.extensions.workers.Workers

class NoteItemsAdapter(private var item: Note) : INoteItemsAdapter,
        RecyclerView.Adapter<NoteItemsAdapter.NoteItemsHolder>(), RCScope {

    private val itemTypes = arrayListOf<Int>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteItemsHolder {
        return NoteItemsHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.note_item_type, parent, false))
    }

    override fun getItemCount(): Int = itemTypes.size

    override fun onBindViewHolder(holder: NoteItemsHolder, position: Int) {
        holder.itemView.contentImage.apply {
            setImageDrawable(resources.getDrawable(when (itemTypes[holder.adapterPosition]) {
                Types.ITEM_TYPE_IMAGE -> R.drawable.main_screen_note_image
                Types.ITEM_TYPE_NOTE -> R.drawable.main_screen_note_text
                Types.ITEM_TYPE_LIST -> R.drawable.main_screen_note_list
                Types.ITEM_TYPE_AUDIO -> R.drawable.main_screen_note_audio
                Types.ITEM_TYPE_ALARM -> R.drawable.main_screen_note_alarm
                Types.ITEM_TYPE_NOTIFICATION -> R.drawable.main_screen_note_notification
                Types.ITEM_TYPE_SEPARATOR -> R.drawable.main_screen_categories_connector
                else -> 0
            }))
        }
    }

    override fun updateItem(note: Note) {
        this.item = note
    }

    override fun updateItemContent() {
        worker {
            itemTypes.clear()
        }.doOnComplete {
            singleDedicated {
                val items = item.entities
                        .filter { (it !is ContentName) and (it !is ContentPriority) and (it !is ContentTags) }
                        .distinctBy { it::class }
                        .map {
                            when (it) {
                                is ContentImage -> Types.ITEM_TYPE_IMAGE
                                is ContentNote -> Types.ITEM_TYPE_NOTE
                                is ContentList -> Types.ITEM_TYPE_LIST
                                is ContentAudio -> Types.ITEM_TYPE_AUDIO
                                else -> Types.ITEM_TYPE_NOTE
                            }
                        }.toMutableList()
                when (item.params!!.type) {
                    Types.NOTE_TYPE_NOTIFICATION -> items.add(Types.ITEM_TYPE_NOTIFICATION)
                    Types.NOTE_TYPE_ALARM -> items.add(Types.ITEM_TYPE_ALARM)
                }
                return@singleDedicated items.toList()
            }.subscribe(object : Subscription<List<Int>> {
                override fun onComplete(result: List<Int>) {
                    if (result.isNotEmpty()) {
                        (0 until ((result.size * 2) - 1)).forEach {
                            itemTypes.add(if (it % 2 == 0) result[it / 2] else Types.ITEM_TYPE_SEPARATOR)
                        }
                    }
                }
            }, Workers.default())
                    .doOnComplete {
                        dataChange()
                    }
                    .completeOn(Workers.ui())
                    .execute()
        }.completeOn(Workers.ui()).execute()
    }

    inner class NoteItemsHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)

    override fun dataChange() = notifyDataSetChanged()

    override fun itemInserted(position: Int) = notifyItemInserted(position)

    override fun itemsRangeInserted(start: Int, amount: Int) = notifyItemRangeInserted(start, amount)

    override fun itemUpdated(position: Int) = notifyItemChanged(position)
}