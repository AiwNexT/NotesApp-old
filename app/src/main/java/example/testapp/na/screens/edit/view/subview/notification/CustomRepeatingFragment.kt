package example.testapp.na.screens.edit.view.subview.notification

import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.Button
import android.widget.EditText
import butterknife.BindView
import butterknife.BindViews
import butterknife.OnClick
import example.testapp.na.R
import example.testapp.na.screens.edit.interactor.IEditInteractor
import example.testapp.na.screens.edit.presenter.IEditPresenter
import example.testapp.na.screens.edit.router.IEditRouter
import example.testapp.na.screens.edit.view.IEditView
import example.testapp.na.tools.credentials.Dates
import example.testapp.na.tools.elements.MutablePair
import example.testapp.na.vipercore.view.BaseFragment
import rc.extensions.streaming.Subscription
import rc.extensions.workers.Workers
import javax.inject.Inject

class CustomRepeatingFragment: BaseFragment(R.layout.fragment_repeating_custom), ICustomRepeating {

    @BindViews(R.id.minutesIntervalBtn,
            R.id.hoursIntervalBtn,
            R.id.daysIntervalBtn,
            R.id.weeksIntervalBtn,
            R.id.monthsIntervalBtn,
            R.id.yearsIntervalBtn)
    lateinit var intervalsBtns: Array<Button>

    @BindView(R.id.repeatingInterval) lateinit var repeatingInterval: EditText

    @Inject
    lateinit var presenter: IEditPresenter<IEditView, IEditInteractor, IEditRouter>

    private var interval: MutablePair<Long, Long> = MutablePair()

    override fun onFragmentCreated(view: View) {
        provideComponent().inject(this)
        cleanupInterval()
    }

    override fun cleanupInterval() {
        presenter.loadInterval().subscribe(object : Subscription<Pair<Long, Long>> {
            override fun onComplete(result: Pair<Long, Long>) {
                interval.first = result.first
                interval.second = result.second
                if (interval.second == 0L) {
                    cleanViewsStates()
                } else {
                    val selected = getViewFromPeriod(interval.second!!)
                    selected.viewState(true)
                    intervalsBtns.filter { it != selected }.forEach {
                        it.viewState(false)
                    }
                }
            }
        }, Workers.default())
                .doOnComplete {
                    intervalUpdates()
                }.completeOn(Workers.ui())
                .execute()
    }

    override fun intervalUpdates() {
        repeatingInterval.setText(interval.first!!.let { if (it == 0L) "" else it.toString() })
        repeatingInterval.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                if (repeatingInterval.text.isNotEmpty()) {
                    interval.first = repeatingInterval.text.toString().toLong()
                } else {
                    interval.first = 0L
                }
                presenter.onIntervalUpdate(interval.first!!, interval.second!!)
            }
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })
    }

    @OnClick(R.id.minutesIntervalBtn,
            R.id.hoursIntervalBtn,
            R.id.daysIntervalBtn,
            R.id.weeksIntervalBtn,
            R.id.monthsIntervalBtn,
            R.id.yearsIntervalBtn)
    override fun onChangeInterval(v: View) {
        interval.second = getPeriodFromView(v)
        val selected = getViewFromPeriod(interval.second!!)
        selected.viewState(true)
        intervalsBtns.filter { it != selected }.forEach {
            it.viewState(false)
        }
        presenter.onIntervalUpdate(interval.first!!, interval.second!!)
    }

    private fun cleanViewsStates() {
        intervalsBtns.forEach { it.viewState(false) }
    }

    private fun Button.viewState(selected: Boolean) {
        setBackgroundDrawable(resources.getDrawable(if (selected)
            R.drawable.bottom_sheet_extended_active_option
        else R.drawable.bottom_sheet_extended_inactive_option))
    }

    private fun getViewFromPeriod(period: Long) = intervalsBtns.first { it.id == when (period) {
        Dates.MINUTES -> R.id.minutesIntervalBtn
        Dates.HOURS -> R.id.hoursIntervalBtn
        Dates.DAYS -> R.id.daysIntervalBtn
        Dates.WEEKS -> R.id.weeksIntervalBtn
        Dates.MONTHS -> R.id.monthsIntervalBtn
        Dates.YEARS -> R.id.yearsIntervalBtn
        else -> R.id.minutesIntervalBtn
    } }

    private fun getPeriodFromView(v: View): Long = when(v.id) {
        R.id.minutesIntervalBtn -> Dates.MINUTES
        R.id.hoursIntervalBtn -> Dates.HOURS
        R.id.daysIntervalBtn -> Dates.DAYS
        R.id.weeksIntervalBtn -> Dates.WEEKS
        R.id.monthsIntervalBtn -> Dates.MONTHS
        R.id.yearsIntervalBtn -> Dates.YEARS
        else -> Dates.MINUTES
    }
}