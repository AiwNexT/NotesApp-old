package example.testapp.na.screens.edit.view

import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import com.loopeer.itemtouchhelperextension.Extension
import com.loopeer.itemtouchhelperextension.ItemTouchHelperExtension
import example.testapp.na.R
import example.testapp.na.data.entities.notes.notescontent.ContentList
import example.testapp.na.screens.edit.view.support.IListController
import example.testapp.na.screens.edit.view.support.ListController
import example.testapp.na.tools.custom.views.ListCallbackController
import example.testapp.na.tools.extensions.swap
import kotlinx.android.synthetic.main.edit_list_item.view.*
import rc.extensions.scope.RCScope

class ListAdapter(override var viewID: Int,
                  var offsetInParent: Int,
                  private var item: ContentList) :
        IList, RCScope, RecyclerView.Adapter<ListAdapter.ListHolder>() {

    internal lateinit var parent: IEditView
    private lateinit var controller: IListController

    private lateinit var itemTouchHelperExtension: ItemTouchHelperExtension

    override fun onViewCreated() {}

    override fun attachParentView(view: IEditView) {
        this.parent = view
        this.controller = ListController(this, item)

    }

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        val itemTouchHelperCallback = ListCallbackController(object :
                ListCallbackController.RecyclerItemTouchHelperListener {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int, position: Int) {}
            override fun onMoved(from: Int, to: Int) {
                item.items.swap(from, to)
                notifyItemMoved(from, to)
            }
        }, ItemTouchHelper.END)
        itemTouchHelperExtension = ItemTouchHelperExtension(itemTouchHelperCallback).apply {
            attachToRecyclerView(recyclerView)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListHolder {
        return ListHolder(LayoutInflater.from(parent.context)
                .inflate(viewID, parent, false)).apply {
            ButterKnife.bind(this@ListAdapter, itemView)
        }
    }

    inner class ListHolder(itemView: View?) : RecyclerView.ViewHolder(itemView), Extension {
        var removeNote: View? = null
        init {
            removeNote = itemView?.findViewById(R.id.removeNote)
        }

        override fun getActionWidth(): Float {
            return removeNote?.width?.toFloat() ?: 0f
        }
    }

    override fun getItemCount(): Int {
        return item.items.size
    }

    override fun onBindViewHolder(holder: ListHolder, position: Int) {
        controller.bindRemoveListener(holder.itemView.editListItemDelete, holder.adapterPosition)
        controller.bindCheckbox(holder.itemView.editListItemCheckbox,
                holder.itemView.editListItemText, holder.adapterPosition)
        controller.bindText(holder.itemView.editListItemText, holder.adapterPosition)
        holder.removeNote?.setOnClickListener {
            item.items.removeAt(holder.adapterPosition)
            notifyItemRemoved(holder.adapterPosition)
            itemTouchHelperExtension.closeOpened()
        }
    }

    override fun dataChange() {
        notifyDataSetChanged()
    }

    override fun itemInserted(position: Int) {
        notifyItemInserted(position)
    }

    override fun itemsRangeInserted(start: Int, amount: Int) {
        notifyItemRangeInserted(start, amount)
    }

    override fun itemUpdated(position: Int) {
        notifyItemChanged(position)
    }
}