package example.testapp.na.screens.main.view

import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import example.testapp.na.R
import example.testapp.na.core.managers.support.INoteItemViewHelper
import example.testapp.na.data.entities.notes.Note
import example.testapp.na.data.entities.notes.notescontent.ContentName
import example.testapp.na.data.entities.notes.notescontent.ContentPriority
import example.testapp.na.tools.credentials.Priorities
import example.testapp.na.tools.elements.MutablePair
import kotlinx.android.synthetic.main.item_note.view.*
import rc.extensions.handlers.single.SingleSubscriber
import rc.extensions.handlers.single.Task
import rc.extensions.scope.RCScope
import rc.extensions.scope.singleWorker
import rc.extensions.scope.ui
import rc.extensions.scope.worker
import rc.extensions.streaming.Subscription
import rc.extensions.workers.Workers

class NotesAdapter(private val view: IMainView,
                   override val viewID: Int,
                   private val notes: ArrayList<MutablePair<Note, INoteItemViewHelper>>) :
        INotesAdapter, RCScope, RecyclerView.Adapter<NotesAdapter.NotesHolder>() {

    private val innerAdapters = arrayListOf<INoteContentAdapter>()
    private val itemsAdapters = arrayListOf<INoteItemsAdapter>()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int) =
            NotesHolder(LayoutInflater.from(parent.context)
                    .inflate(viewID, parent, false).apply {
                        arrayOf(noteContent, noteContentItems).forEach {
                            it.setHasFixedSize(true)
                            it.layoutManager = LinearLayoutManager(context,
                                    LinearLayoutManager.VERTICAL, false)
                        }
                    })

    override fun getItemCount() = notes.size

    override fun onBindViewHolder(holder: NotesHolder, position: Int) {
        holder.itemView.noteContent.apply {
            (innerAdapters[holder.adapterPosition] as NoteContentAdapter).apply {
                adapter = this
                dataChange()
            }
        }
        holder.itemView.noteContentItems.apply {
            with(itemsAdapters[holder.adapterPosition] as NoteItemsAdapter) {
                adapter = this
                updateItemContent()
            }
        }
        attachDataToNote(holder.itemView, holder.adapterPosition, {
            holder.itemView.apply {
                notes[holder.adapterPosition].second?.update(this@apply, noteContent,
                        noteName.let { if (it.visibility == View.GONE) 0 else it.height } +
                                noteTimeRepeating.let { if (it.visibility == View.GONE) 0 else it.height },
                        innerAdapters[holder.adapterPosition])
            }
        })
        holder.itemView.noteClicker.setOnClickListener {
            view.onNoteClicked(notes[holder.adapterPosition].first!!.id)
        }
        blurSecured(holder, position)
    }

    private fun attachDataToNote(view: View, position: Int, onEnd: () -> Unit) {
        getNoteName(position).subscribe(object : Subscription<String?> {
            override fun onComplete(result: String?) {
                view.noteName.text = result
                view.noteNameBlur.text = result
                result?.let { if (it.isEmpty()) view.noteName.visibility = View.GONE }
            }
        }, Workers.ui())
                .doOnComplete {
                    getNoteColor(position).subscribe(object : Subscription<Int> {
                        override fun onComplete(result: Int) {
                            view.noteColor.setBackgroundColor(view.resources.getColor(result))
                        }
                    }, Workers.ui())
                            .doOnComplete {
                                getNoteRepeating(position).subscribe(object : Subscription<String?> {
                                    override fun onComplete(result: String?) {
                                        view.noteTimeRepeating.apply {
                                            text = result
                                            result?.let { if (it.isEmpty()) view.noteTimeRepeating.visibility = View.GONE }
                                        }
                                    }
                                }, Workers.ui())
                                        .completeOn(Workers.ui())
                                        .doOnComplete {
                                            onEnd()
                                        }
                                        .execute()
                            }
                            .completeOn(Workers.ui())
                            .execute()
                }.completeOn(Workers.ui())
                .execute()
    }

    private fun blurSecured(holder: NotesHolder, position: Int) = worker {
        notes[position].first?.params?.locked!!.let {
            if (it) {
                ui {
                    holder.itemView.noteContentBlur.visibility = View.VISIBLE
                }.execute()
            } else {
                ui {
                    holder.itemView.noteContentBlur.visibility = View.GONE
                }.execute()
            }
        }
    }.execute()

    private fun getNoteName(position: Int): SingleSubscriber<String?> {
        return singleWorker {
            return@singleWorker (notes[position]
                    .first?.entities?.first { it is ContentName } as ContentName).name
        }
    }

    private fun getNoteRepeating(position: Int): SingleSubscriber<String?> {
        return singleWorker {
            return@singleWorker (notes[position].first?.params
                    ?.let { if (it.timeMillis > 0) "${it.date}, ${it.time}" else "" })
        }
    }

    private fun getNoteColor(position: Int): SingleSubscriber<Int> {
        return singleWorker {
            notes[position].first!!.let {
                return@let if (it.params?.locked!!) R.color.colorLocked else
                when {
                    it.params?.pinned!! -> R.color.colorPinned
                    else -> when ((it.entities.first { it is ContentPriority } as ContentPriority).priority) {
                        Priorities.LOW -> R.color.colorLow
                        Priorities.MEDIUM -> R.color.colorMedium
                        Priorities.HIGH -> R.color.colorHigh
                        Priorities.URGENT -> R.color.colorUrgent
                        else -> R.color.colorLow
                    }
                }
            }
        }
    }

    inner class NotesHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)

    private fun createInnerAdapters(): Task {
        return worker {
            notes.forEach {
                innerAdapters.add(NoteContentAdapter(view, it.first!!))
                itemsAdapters.add(NoteItemsAdapter(it.first!!))
            }
        }
    }

    override fun itemRemoved(position: Int) {
        worker {
            innerAdapters.removeAt(position)
            itemsAdapters.removeAt(position)
        }.doOnComplete { notifyItemRemoved(position) }
                .completeOn(Workers.ui())
                .execute()
    }

    override fun dataChange() {
        notifyDataSetChanged()
    }

    override fun itemInserted(position: Int) {
        worker {
            innerAdapters.add(position, NoteContentAdapter(view, notes[position].first!!))
            itemsAdapters.add(position, NoteItemsAdapter(notes[position].first!!))
        }.doOnComplete {
            notifyItemInserted(position)
        }.completeOn(Workers.ui()).execute()
    }

    override fun itemsRangeInserted(start: Int, amount: Int) {
        createInnerAdapters()
                .completeOn(Workers.ui())
                .doOnComplete { notifyItemRangeInserted(start, amount) }
                .execute()
    }

    override fun itemUpdated(position: Int) {
        worker {
            innerAdapters[position].updateNote(notes[position].first!!)
        }.completeOn(Workers.ui())
                .doOnComplete { notifyItemChanged(position) }
                .execute()
    }

    override fun itemUpdatedWithContentChange(position: Int, note: Note) {
        worker {
            itemsAdapters[position].updateItem(note)
        }.doOnComplete {
            itemUpdated(position)
        }.execute()
    }

    override fun itemMoved(from: Int, to: Int) {
        notifyItemMoved(from, to)
    }
}