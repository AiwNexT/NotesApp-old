package example.testapp.na.screens.edit.view.support

import android.text.InputType
import android.view.KeyEvent
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import example.testapp.na.data.entities.notes.notescontent.ContentTags
import example.testapp.na.screens.edit.view.TagsAdapter
import example.testapp.na.tools.custom.views.ListText
import example.testapp.na.tools.extensions.e

class TagsController(private val list: TagsAdapter,
                     private val item: ContentTags) : ITagsController {

    override fun bindText(editable: EditText, position: Int) {
        editable.apply {
            setRawInputType(InputType.TYPE_CLASS_TEXT)
            setText(item.tags[position])
            setChange(editable, position)
            setAddNew(editable, position)
            setClear(editable, position)
        }
    }

    private fun setChange(editable: EditText, position: Int) = editable.apply {
        (this as ListText).setKeyDownListener { code ->
            e { item.tags[position] = text.toString() }
        }
    }

    private fun setClear(editable: EditText, position: Int) {
        editable.apply {
            setOnKeyListener { v, keyCode, event ->
                if ((event.action == KeyEvent.ACTION_DOWN) and
                        (keyCode == KeyEvent.KEYCODE_DEL) and
                        (selectionStart == 0)) {
                    selectionStart.apply {
                        val toTransfer = text.substring(this)
                        if (item.tags.size > 1) {
                            if (position > 0) {
                                item.tags[position - 1] += toTransfer
                                list.itemUpdated(position - 1)
                            }
                            item.tags.removeAt(position)
                            list.notifyItemRemoved(position)
                            list.notifyItemRangeChanged(0, item.tags.size)
                        }
                    }
                    return@setOnKeyListener false
                } else {
                    return@setOnKeyListener false
                }
            }
        }
    }

    private fun setAddNew(editable: EditText, position: Int) = editable.apply {
        setOnEditorActionListener { v, actionId, event ->
            if ((event != null && (event.keyCode == KeyEvent.KEYCODE_ENTER)) ||
                    (actionId == EditorInfo.IME_ACTION_DONE)) {
                if (text.isNotEmpty()) {
                    (editable as ListText).sendEvent(KeyEvent.KEYCODE_ENTER)
                    list.parent.onAddTagItemClicked(list.offsetInParent, position)
                } else {
                    list.parent.emptyTagCreation()
                }
            }
            return@setOnEditorActionListener false
        }
    }
}