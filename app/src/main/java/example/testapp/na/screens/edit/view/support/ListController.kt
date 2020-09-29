package example.testapp.na.screens.edit.view.support

import android.graphics.Paint
import android.text.InputType
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import example.testapp.na.data.entities.notes.notescontent.ContentList
import example.testapp.na.screens.edit.view.ListAdapter
import example.testapp.na.tools.custom.views.ListText
import example.testapp.na.tools.extensions.e
import example.testapp.na.tools.extensions.swap

class ListController(private val list: ListAdapter,
                     private val item: ContentList) : IListController {

    override fun bindText(editable: EditText, position: Int) {
        editable.apply {
            setRawInputType(InputType.TYPE_CLASS_TEXT)
            setText(item.items[position].item)
            setChanged(editable, position)
            setBackKeyListener(editable, position)
            setActionDone(editable, position)
        }
    }

    override fun bindRemoveListener(removeItem: ImageView, position: Int) {
        removeItem.setOnClickListener {
            if (item.items.size == 1) {
                item.items.clear()
                list.dataChange()
                list.parent.onListCleared(list.offsetInParent)
            } else if (item.items.size > 1) {
                if (position > 0) {
                    list.itemUpdated(position - 1)
                }
                item.items.removeAt(position)
                list.notifyItemRemoved(position)
                list.notifyItemRangeChanged(0, item.items.size)
            }
        }
    }

    private fun setChanged(editable: EditText, position: Int) = editable.apply {
        (this as ListText).setKeyDownListener { code ->
            if (code != KeyEvent.KEYCODE_DEL) {
                e { item.items[position].item = text.toString() }
            }
        }
    }

    private fun setBackKeyListener(editable: EditText, position: Int) {
        editable.apply {
            setOnKeyListener { v, keyCode, event ->
                if ((event.action == KeyEvent.ACTION_DOWN) and
                        (keyCode == KeyEvent.KEYCODE_DEL) and
                        (selectionStart == 0)) {
                    selectionStart.apply {
                        val toTransfer = text.substring(this)
                        if (item.items.size == 1) {
                            item.items.clear()
                            list.dataChange()
                            list.parent.onListCleared(list.offsetInParent)
                        } else if (item.items.size > 1) {
                            if (position > 0) {
                                item.items[position - 1].item += toTransfer
                                list.itemUpdated(position - 1)
                            }
                            item.items.removeAt(position)
                            list.notifyItemRemoved(position)
                            list.notifyItemRangeChanged(0, item.items.size)
                        }
                    }
                    return@setOnKeyListener false
                } else {
                    return@setOnKeyListener false
                }
            }
        }
    }

    private fun setActionDone(editable: EditText, position: Int) = editable.apply {
        setOnEditorActionListener { _, actionId, event ->
            if ((event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER)) ||
                    (actionId == EditorInfo.IME_ACTION_DONE)) {
                var toTransfer = ""
                text.isEmpty()
                        .let { if (it) 0 else selectionStart }
                        .apply {
                            toTransfer = text.substring(this)
                            item.items[position].item = text.substring(0, this)
                            setText(item.items[position].item)
                        }
                list.parent.onAddListItemClicked(list.offsetInParent, position, toTransfer)
                return@setOnEditorActionListener true
            } else {
                return@setOnEditorActionListener false
            }
        }
    }

    override fun bindCheckbox(checkBox: CheckBox, editable: EditText, position: Int) {
        checkBox.let {
            item.items[position].apply {
                it.isChecked = this.checked
                checkText(editable, this.checked)
                it.setOnClickListener {
                    if (this.item != editable.text.toString()) {
                        this.item = editable.text.toString()
                    }
                    checkText(editable, this.checked)
                    if (!this.checked) {
                        checkedToEnd(position)
                    } else {
                        checkBack(position)
                    }
                }
            }
        }
    }

    private fun checkBack(position: Int) {
        (item.items.size > 1).let {
            if (it) {
                val lastNotChecked = item.items.indexOf(item.items.lastOrNull { !it.checked }
                        ?: item.items[position])
                if (lastNotChecked != position) {
                    if (item.items.size - 1 - lastNotChecked > 1) {
                        item.items.swap(lastNotChecked + 1, position)
                    }
                    item.items[lastNotChecked + 1].apply { checked = !checked }
                    arrayOf(position, lastNotChecked + 1).forEach { list.itemUpdated(it) }
                } else {
                    item.items[lastNotChecked].apply { checked = !checked }
                    item.items.swap(0, lastNotChecked)
                    arrayOf(0, lastNotChecked).forEach { list.itemUpdated(it) }
                }
            } else {
                item.items.last().apply { checked = !checked }
                list.itemUpdated(item.items.size - 1)
            }
        }
    }

    private fun checkedToEnd(position: Int) {
        (item.items.size > 1).let {
            if (it) {
                val next = item.items[position]
                item.items.removeAt(position)
                item.items.add(next)
            }
            item.items.last().apply { checked = !checked }
            list.notifyItemRangeChanged(0, item.items.size)
        }
    }

    private fun checkText(editable: EditText, state: Boolean) {
        editable.apply {
            paintFlags = if (state) {
                (paintFlags or Paint.STRIKE_THRU_TEXT_FLAG)
            } else {
                (paintFlags and Paint.STRIKE_THRU_TEXT_FLAG.inv())
            }
        }
    }
}