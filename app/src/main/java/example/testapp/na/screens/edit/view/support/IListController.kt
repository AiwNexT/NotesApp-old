package example.testapp.na.screens.edit.view.support

import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView

interface IListController {

    fun bindText(editable: EditText, position: Int)

    fun bindCheckbox(checkBox: CheckBox, editable: EditText, position: Int)

    fun bindRemoveListener(removeItem: ImageView, position: Int)
}