package example.testapp.na.screens.main.view

import android.graphics.Paint
import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import example.testapp.na.R
import example.testapp.na.data.entities.notes.notescontent.ContentList
import kotlinx.android.synthetic.main.main_list_item.view.*

class MainListAdapter(private val content: ContentList) :
        IMainList, RecyclerView.Adapter<MainListAdapter.MainListHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MainListHolder {
        return MainListHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.main_list_item, parent, false))
    }

    override fun getItemCount(): Int {
        return content.items.size.let { if (it > 3) 3 else it }
    }

    override fun onBindViewHolder(holder: MainListHolder, position: Int) {
        holder.itemView.mainListItemText.text = content.items[position].item
        holder.itemView.mainListItemText.apply {
            paintFlags = if (content.items[position].checked) {
                (paintFlags or Paint.STRIKE_THRU_TEXT_FLAG)
            } else {
                (paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv())
            }
        }
    }

    inner class MainListHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)

    override fun dataChange() = notifyDataSetChanged()

    override fun itemInserted(position: Int) = notifyItemInserted(position)

    override fun itemsRangeInserted(start: Int, amount: Int) = notifyItemRangeInserted(start, amount)

    override fun itemUpdated(position: Int) = notifyItemChanged(position)
}