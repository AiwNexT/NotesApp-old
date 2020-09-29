package example.testapp.na.screens.edit.view

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.view.View
import butterknife.OnClick
import example.testapp.na.R
import example.testapp.na.vipercore.view.BaseActivity
import example.testapp.na.core.managers.app.AnimationsHelper
import example.testapp.na.core.managers.app.SheetHelper
import example.testapp.na.core.managers.app.SnackbarHelper
import example.testapp.na.screens.edit.interactor.IEditInteractor
import example.testapp.na.screens.edit.presenter.IEditPresenter
import example.testapp.na.screens.edit.router.IEditRouter
import example.testapp.na.tools.credentials.Requests
import example.testapp.na.tools.credentials.Types
import example.testapp.na.tools.extensions.dpToPx
import kotlinx.android.synthetic.main.activity_edit.*
import kotlinx.android.synthetic.main.toolbar_edit.*
import javax.inject.Inject

class EditActivity : BaseActivity(R.layout.activity_edit), IEditView {

    @Inject
    lateinit var presenter: IEditPresenter<IEditView, IEditInteractor, IEditRouter>

    @Inject
    lateinit var snacks: SnackbarHelper

    @Inject
    lateinit var sheets: SheetHelper

    @Inject
    lateinit var animations: AnimationsHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        buildComponent().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated() {
        presenter.onAttach(this)
        bindEditView()
    }

    override fun bindEditView() {
        editRecycler.apply {
            setHasFixedSize(false)
            layoutManager = LinearLayoutManager(this@EditActivity,
                    LinearLayoutManager.VERTICAL, false)
        }
        EditAdapter(this).apply {
            attachParentView(this@EditActivity).execute()
            editRecycler.adapter = this
            val layouts = createEditorLayouts()
            this.attachLayouts(layouts)
            presenter.attachEditAdapter(this)
            presenter.onListInit(editRecycler)
        }
    }

    @OnClick(R.id.editAddNote)
    override fun onAddNoteClicked(view: View) {
        presenter.addNote()
    }

    @OnClick(R.id.editAddList)
    override fun onAddListClicked(view: View) {
        presenter.addList()
    }

    override fun onAddListItemClicked(offset: Int, after: Int, add: String) {
        presenter.addListItem(offset, after, add)
    }

    override fun onAddTagItemClicked(offset: Int, after: Int, add: String) {
        presenter.addTagItem(offset, after, add)
    }

    @OnClick(R.id.editAddAudio)
    override fun onAddAudioClicked(view: View) {
        presenter.addAudio()
    }

    @OnClick(R.id.editAddImage)
    override fun onAddImageClicked(view: View) {
        presenter.addImage()
    }

    override fun createEditorLayouts(): ArrayList<Pair<Int, Int>> {
        return arrayListOf<Pair<Int, Int>>().apply {
            add(Pair(Types.LAYOUT_PRIORITY, R.layout.edit_item_priority))
            add(Pair(Types.LAYOUT_NAME, R.layout.edit_item_name))
            add(Pair(Types.LAYOUT_NOTE, R.layout.edit_item_note))
            add(Pair(Types.LAYOUT_AUDIO, R.layout.edit_item_audio))
            add(Pair(Types.LAYOUT_TAGS, R.layout.edit_item_tags))
            add(Pair(Types.LAYOUT_LIST, R.layout.edit_item_list))
            add(Pair(Types.LAYOUT_IMAGE, R.layout.edit_item_image))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == Requests.PICK_IMAGE &&
                resultCode == AppCompatActivity.RESULT_OK &&
                data != null && data.data != null) {
            presenter.onImagePicked(data.data)
        }
    }

    override fun getListManager(): RecyclerView.LayoutManager = editRecycler.layoutManager

    override fun emptyTagCreation() {
        presenter.onEmptyTag()
    }

    override fun showSnack(message: String) {
        snacks.show(editRecycler, message, {})
    }

    override fun showSnackOnChild(child: View, message: String) {
        snacks.show(child, message, {
            animations.hideObjectByHeight(child, {})
        })
        animations.showObjectByHeight(child, child.dpToPx(56), {})
    }

    override fun onListCleared(offset: Int) {
        presenter.onClearList(offset)
    }

    override fun onPriorityClicked() {
        sheets.showPriorityPicker(supportFragmentManager)
    }

    override fun onItemRemoveClicked(position: Int) {
        presenter.removeItem(position)
    }

    @OnClick(R.id.archiveNote,
            R.id.pinNote,
            R.id.addNotification,
            R.id.lockNote)
    override fun onToolbarControlClicked(which: View) {
        when (which.id) {
            R.id.archiveNote -> presenter.archiveNote()
            R.id.lockNote -> presenter.lockNote()
            R.id.pinNote -> presenter.pinNote()
            R.id.addNotification -> onNotificationClicked()
        }
    }

    override fun onNotificationClicked() {
        sheets.showNotificationPickerSheet(supportFragmentManager)
    }

    override fun changePinState(state: Boolean) {
        pinNote.setImageDrawable(resources.getDrawable(
                if (state) R.drawable.add_note_pinned_button
                else R.drawable.add_note_unpinned_button))
    }

    override fun changeLockState(state: Boolean) {
        lockNote.setImageDrawable(resources.getDrawable(
                if (state) R.drawable.add_note_locked_button
                else R.drawable.add_note_unlocked_button))
    }

    override fun changeNotificationState(state: Boolean) {
        addNotification.setImageDrawable(resources.getDrawable(
                if (state) R.drawable.add_note_belled_icon
                else R.drawable.add_note_unbelled_icon))
    }

    @OnClick(R.id.back)
    override fun onBackPressed() {
        presenter.backToNotesList()
    }

    override fun onDestroy() {
        presenter.onDetach()
        super.onDestroy()
    }
}