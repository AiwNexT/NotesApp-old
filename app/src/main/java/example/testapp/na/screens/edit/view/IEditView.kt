package example.testapp.na.screens.edit.view

import android.support.v7.widget.RecyclerView
import android.view.View
import example.testapp.na.vipercore.view.ActivityView

interface IEditView: ActivityView {

    fun bindEditView()

    fun onAddNoteClicked(view: android.view.View)

    fun onAddListClicked(view: android.view.View)

    fun onAddListItemClicked(offset: Int, after: Int, add: String = "")

    fun onAddTagItemClicked(offset: Int, after: Int, add: String = "")

    fun onAddAudioClicked(view: android.view.View)

    fun onAddImageClicked(view: android.view.View)

    fun createEditorLayouts(): ArrayList<Pair<Int, Int>>

    fun getListManager(): RecyclerView.LayoutManager

    fun emptyTagCreation()

    fun showSnack(message: String)

    fun showSnackOnChild(child: View, message: String)

    fun onListCleared(offset: Int)

    fun onPriorityClicked()

    fun onNotificationClicked()

    fun onToolbarControlClicked(which: View)

    fun changePinState(state: Boolean)

    fun changeLockState(state: Boolean)

    fun changeNotificationState(state: Boolean)

    fun onItemRemoveClicked(position: Int)
}