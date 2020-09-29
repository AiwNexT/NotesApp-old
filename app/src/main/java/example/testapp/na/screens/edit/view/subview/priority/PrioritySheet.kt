package example.testapp.na.screens.edit.view.subview.priority

import android.view.View
import android.widget.TextView
import butterknife.OnClick
import example.testapp.na.R
import example.testapp.na.vipercore.view.BaseSheet
import example.testapp.na.screens.edit.interactor.IEditInteractor
import example.testapp.na.screens.edit.presenter.IEditPresenter
import example.testapp.na.screens.edit.router.IEditRouter
import example.testapp.na.screens.edit.view.IEditView
import example.testapp.na.tools.credentials.Priorities
import kotlinx.android.synthetic.main.sheet_priority_picker.view.*
import rc.extensions.streaming.Subscription
import rc.extensions.workers.Workers
import javax.inject.Inject

class PrioritySheet : BaseSheet(R.layout.sheet_priority_picker), IPrioritySheet {

    @Inject
    lateinit var presenter: IEditPresenter<IEditView, IEditInteractor, IEditRouter>

    override fun onDialogCreated(view: View) {
        provideComponent().inject(this)
        setUpPriorities(view)
    }

    override fun setUpPriorities(view: View) {
        val views = view.let {
            arrayListOf(it.priorityLow,
                    it.priorityMedium,
                    it.priorityHigh,
                    it.priorityUrgent)
        }
        presenter.buildPrioritiesPicker()
                .subscribe(object : Subscription<ArrayList<Int>> {
                    override fun onComplete(result: ArrayList<Int>) {
                        setViews(views, result)
                    }
                }, Workers.ui())
                .execute()
    }

    private fun setViews(views: ArrayList<TextView>, priorities: ArrayList<Int>) {
        views.forEachIndexed { index, v ->
            v.setBackgroundDrawable(resources.getDrawable(priorities[index]))
        }
    }

    @OnClick(R.id.priorityLow, R.id.priorityMedium,
            R.id.priorityHigh, R.id.priorityUrgent)
    fun priorityClick(v: View) {
        onPriorityClicked(when (v.id) {
            R.id.priorityLow -> Priorities.LOW
            R.id.priorityMedium -> Priorities.MEDIUM
            R.id.priorityHigh -> Priorities.HIGH
            R.id.priorityUrgent -> Priorities.URGENT
            else -> Priorities.LOW
        })
    }

    override fun onPriorityClicked(which: Int) {
        presenter.onPriorityPicked(which)
        hide()
    }
}