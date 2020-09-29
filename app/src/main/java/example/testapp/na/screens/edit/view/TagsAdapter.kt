package example.testapp.na.screens.edit.view

import android.support.v7.widget.RecyclerView
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import example.testapp.na.R
import example.testapp.na.data.entities.notes.notescontent.ContentTags
import example.testapp.na.screens.edit.view.support.ITagsController
import example.testapp.na.screens.edit.view.support.TagsController
import kotlinx.android.synthetic.main.tag_item.view.*
import rc.extensions.scope.RCScope

class TagsAdapter(var offsetInParent: Int,
                  var item: ContentTags) : ITags, RCScope, RecyclerView.Adapter<TagsAdapter.TagsHolder>() {

    internal lateinit var parent: IEditView
    private lateinit var controller: ITagsController

    override fun onViewCreated() {}

    override fun attachParentView(view: IEditView) {
        this.parent = view
        this.controller = TagsController(this, item)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TagsHolder {
        return TagsHolder(LayoutInflater.from(parent.context)
                .inflate(R.layout.tag_item, parent, false)).apply {
            ButterKnife.bind(this@TagsAdapter, itemView)
        }
    }

    override fun getItemCount(): Int {
        return item.tags.size
    }

    override fun onBindViewHolder(holder: TagsHolder, position: Int) {
        controller.bindText(holder.itemView.editNoteTagText, holder.adapterPosition)
    }

    inner class TagsHolder(itemView: View?) : RecyclerView.ViewHolder(itemView)

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