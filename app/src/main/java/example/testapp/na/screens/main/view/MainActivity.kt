package example.testapp.na.screens.main.view

import android.animation.ArgbEvaluator
import android.graphics.Color
import android.os.Build
import android.os.Bundle
import android.support.design.widget.Snackbar
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.StaggeredGridLayoutManager
import android.view.View
import butterknife.BindView
import butterknife.OnClick
import com.yarolegovich.slidingrootnav.SlidingRootNav
import com.yarolegovich.slidingrootnav.SlidingRootNavBuilder
import example.testapp.na.R
import example.testapp.na.core.managers.app.SnackbarManager
import example.testapp.na.core.managers.support.INoteItemViewHelper
import example.testapp.na.data.entities.notes.Note
import example.testapp.na.screens.main.interactor.IMainInteractor
import example.testapp.na.screens.main.presenter.IMainPresenter
import example.testapp.na.screens.main.router.IMainRouter
import example.testapp.na.tools.credentials.Colors
import example.testapp.na.tools.credentials.Messages
import example.testapp.na.tools.credentials.Types
import example.testapp.na.tools.custom.groups.IFabMenu
import example.testapp.na.tools.custom.views.GridWrapper
import example.testapp.na.tools.elements.MutablePair
import example.testapp.na.vipercore.view.BaseActivity
import kotlinx.android.synthetic.main.fab_menu_custom.*
import javax.inject.Inject

class MainActivity : BaseActivity(R.layout.activity_main), IMainView {

    @BindView(R.id.notesView)
    lateinit var notesView: RecyclerView

    @Inject
    lateinit var presenter: IMainPresenter<IMainView, IMainInteractor, IMainRouter>

    @Inject
    lateinit var fabsMenu: IFabMenu

    @Inject
    lateinit var snacks: SnackbarManager

    private lateinit var drawerMenu: SlidingRootNav

    private var notesAdapter: INotesAdapter? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        buildComponent().inject(this)
        super.onCreate(savedInstanceState)
    }

    override fun onViewCreated() {
        bindDrawer()
        bindNotesView(true)
        presenter.onAttach(this)
        fabsMenu.attach(this, fabMenu)
    }

    override fun bindDrawer() {
        SlidingRootNavBuilder(this).apply {
            withContentClickableWhenMenuOpened(false)
            withDragDistance(200)
            withRootViewScale(0.9f)
            withRootViewElevation(10)
            withRootViewYTranslation(-15)
            withMenuLayout(R.layout.drawer_main)
            addDragListener { editStatusBar(it) }
            drawerMenu = inject()
        }
    }

    private fun editStatusBar(multiplier: Float) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            window.statusBarColor = ArgbEvaluator().evaluate(multiplier,
                    Color.parseColor(Colors.COLOR_START_INTERPOLATION),
                    Color.parseColor(Colors.COLOR_FINISH_INTERPOLATION))
                    .toString().toInt()
        }
    }

    override fun bindNotesView(isGrid: Boolean) {
        notesView.apply {
            setHasFixedSize(true)
            layoutManager = when (isGrid) {
                true -> {
                    GridWrapper(2,
                            StaggeredGridLayoutManager.VERTICAL)
                }
                false -> {
                    LinearLayoutManager(this@MainActivity,
                            LinearLayoutManager.VERTICAL, false)
                }
            }
            presenter.onListInit(this)
        }
    }

    override fun onDestroy() {
        presenter.onDetach()
        super.onDestroy()
    }

    override fun bindNotesAdapter(notes: ArrayList<MutablePair<Note, INoteItemViewHelper>>) {
        notesAdapter = NotesAdapter(this, R.layout.item_note, notes)
        presenter.attachNotesAdapter(notesAdapter!!)
        notesView.adapter = notesAdapter as NotesAdapter
    }

    @OnClick(R.id.fabAdd)
    override fun onAddClicked() {
        presenter.onAddItemClicked(fabsMenu.currentState)
    }

    @OnClick(R.id.fabCLose)
    override fun onCloseClicked() {
        hideFabMenu()
    }

    override fun onNoteClicked(id: Long) {
        presenter.onNoteClicked(id)
    }

    @OnClick(R.id.mainMenu, R.id.mainSearch, R.id.mainViewType)
    override fun onControlsClicked(view: View) {
        presenter.onControlItemClicked(when (view.id) {
            R.id.mainMenu -> Types.TYPE_MENU
            R.id.mainSearch -> Types.TYPE_SEARCH
            R.id.mainViewType -> Types.TYPE_CHANGE_VIEW
            else -> Types.TYPE_NONE
        })
    }

    @OnClick(R.id.fabAddAlarm, R.id.fabAddList, R.id.fabAddNote, R.id.audioNote, R.id.quickNote)
    override fun onAddNoteClicked(view: View) {
        presenter.onAddNoteItemClicked(when (view.id) {
            R.id.fabAddAlarm -> Types.TYPE_ALARM
            R.id.fabAddList -> Types.TYPE_LIST
            R.id.fabAddNote -> Types.TYPE_NOTE
            R.id.quickNote -> Types.TYPE_QUICK_NOTE
            R.id.audioNote -> Types.TYPE_AUDIO
            else -> Types.TYPE_NOTE
        })
    }

    override fun showFabMenu() = fabsMenu.showMenu()

    override fun hideFabMenu() = fabsMenu.hideMenu()

    override fun openDrawer() = drawerMenu.openMenu()

    override fun closeDrawer() = drawerMenu.closeMenu()

    override fun onBackPressed() {
        if (drawerMenu.isMenuOpened) drawerMenu.closeMenu()
        else super.onBackPressed()
    }

    override fun showAsGrid() {

    }

    override fun showAsList() {

    }

    override fun onArchiveClicked(position: Int) {
        presenter.onArchiveClicked(position)
    }

    override fun showUndoRemovalSnack() {
        snacks.show(notesView, Messages.NOTE_REMOVED, {}, Snackbar.LENGTH_LONG, {
            onUndoRemoval()
        }, Messages.UNDO)
    }

    override fun showSnack(message: String) {
        snacks.show(notesView, message, {})
    }

    override fun onUndoRemoval() {
        presenter.undoRemoval()
    }
}