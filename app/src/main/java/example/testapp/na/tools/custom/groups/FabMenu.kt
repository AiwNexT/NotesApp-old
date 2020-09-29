package example.testapp.na.tools.custom.groups

import android.support.design.widget.FloatingActionButton
import android.view.View
import android.widget.RelativeLayout
import android.widget.TextView
import butterknife.BindView
import butterknife.BindViews
import butterknife.ButterKnife
import example.testapp.na.R
import example.testapp.na.core.managers.app.AnimationsHelper
import example.testapp.na.screens.main.view.IMainView
import rc.extensions.scope.RCScope
import javax.inject.Inject

class FabMenu @Inject internal constructor(private var animationsHelper: AnimationsHelper) : IFabMenu, RCScope {

    @BindViews(R.id.fabAddListLayout, R.id.fabAddAlarmLayout, R.id.fabAddNoteLayout, R.id.fabCloseLayout)
    lateinit var fabsLayouts: Array<RelativeLayout>

    @BindViews(R.id.fabAddNote, R.id.fabAddList, R.id.fabAddAlarm, R.id.fabCLose)
    lateinit var fabs: Array<View>

    @BindView(R.id.fabAdd)
    lateinit var fab: FloatingActionButton

    @BindView(R.id.fabAddLayout)
    lateinit var fabLayout: RelativeLayout

    private var parent: IMainView? = null

    private var menu: View? = null

    override var currentState = false

    override fun attach(parent: IMainView, menu: View) {
        this.parent = parent
        this.menu = menu
        ButterKnife.bind(this, menu)
    }

    override fun showMenu() {
        disableAdd()
        disableSupport()
        fabsLayouts.forEachIndexed { i, it ->
            animationsHelper.slideObjectTop(it, getMeasuredFabHeight(i),
                    if (i == fabsLayouts.size - 1) { { enableAdd(); enableSupport() } } else {{}})
            it.getHint().apply {
                animationsHelper.showObjectByWidth(this, 140, { })
            }
            showCompose()
        }
        changeState()
    }

    override fun hideMenu() {
        disableAdd()
        disableSupport()
        fabsLayouts.forEachIndexed { i, it ->
            animationsHelper.slideObjectBottom(it, getMeasuredFabHeight(i),
                    if (i == fabsLayouts.size - 1) { { enableAdd() } } else { { } })
            it.getHint().apply {
                animationsHelper.hideObjectByWidth(this, { })
            }
            hideCompose()
        }
        changeState()
    }

    private fun hideCompose() {
        fabLayout.getHint().apply {
            animationsHelper.hideObjectByWidth(this, { })
        }
    }

    private fun showCompose() {
        fabLayout.getHint().apply {
            animationsHelper.showObjectByWidth(this, 200, { })
        }
    }

    private fun enableAdd() {
        fab.isClickable = true
    }

    private fun enableSupport() {
        fabs.forEach { it.isClickable = true }
    }

    private fun disableAdd() {
        fab.isClickable = false
    }

    private fun disableSupport() {
        fabs.forEach { it.isClickable = false }
    }

    private fun getMeasuredFabHeight(position: Int) =
            fabs[position].measuredHeight.toFloat() * (position + 1) *
                    when(position) {
                        0 -> 1.35f
                        2 -> 1.26f
                        3 -> 1.25f
                        else -> 1.28f
                    }

    fun changeState() {
        currentState = !currentState
    }

    private fun RelativeLayout.getHint(): TextView {
        return getChildAt(1) as TextView
    }
}