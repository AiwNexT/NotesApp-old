package example.testapp.na.screens.edit.view.subview.notification

import android.view.View
import android.widget.Button
import butterknife.BindViews
import butterknife.OnClick
import example.testapp.na.R
import example.testapp.na.screens.edit.interactor.IEditInteractor
import example.testapp.na.screens.edit.presenter.IEditPresenter
import example.testapp.na.screens.edit.router.IEditRouter
import example.testapp.na.screens.edit.view.IEditView
import example.testapp.na.vipercore.view.BaseFragment
import rc.extensions.streaming.Subscription
import rc.extensions.workers.Workers
import javax.inject.Inject

class WeekdaysRepeatingFragment: BaseFragment(R.layout.fragment_repeating_weekdays), IWeekdaysRepeating {

    @BindViews(R.id.monday, R.id.tuesday,
            R.id.wednesday, R.id.thursday,
            R.id.friday, R.id.saturday, R.id.sunday)
    lateinit var days: Array<Button>

    private var selectedDays = arrayListOf<Int>()

    @Inject
    lateinit var presenter: IEditPresenter<IEditView, IEditInteractor, IEditRouter>

    override fun onFragmentCreated(view: View) {
        provideComponent().inject(this)
        cleanupViews()
    }

    override fun cleanupViews() {
        selectedDays.clear()
        presenter.loadWeekdays().subscribe(object : Subscription<ArrayList<Int>> {
            override fun onComplete(result: ArrayList<Int>) {
                selectedDays = result
            }
        }, Workers.default())
                .doOnComplete {
                    days.forEach { it.changeViewState(selectedDays.contains(getSelected(it))) }
                }
                .completeOn(Workers.ui())
                .execute()
    }

    private fun Button.changeViewState(selected: Boolean) {
        setBackgroundDrawable(resources.getDrawable(if (selected) R.drawable.weekday_picked else R.drawable.weekday_unpicked))
        setTextColor(resources.getColor(if (selected) android.R.color.white else R.color.colorHintLight))
    }

    @OnClick(R.id.monday, R.id.tuesday,
            R.id.wednesday, R.id.thursday,
            R.id.friday, R.id.saturday, R.id.sunday)
    override fun onSelected(v: View) {
        val which = getSelected(v)
        selectedDays.apply {
            if (contains(which)) remove(which) else add(which)
            (v as Button).changeViewState(contains(which))
            presenter.onWeekdaysUpdate(this)
        }
    }

    private fun getSelected(v: View): Int = when (v.id) {
        R.id.monday -> 2
        R.id.tuesday -> 3
        R.id.wednesday -> 4
        R.id.thursday -> 5
        R.id.friday -> 6
        R.id.saturday -> 7
        R.id.sunday -> 1
        else -> 1
    }
}