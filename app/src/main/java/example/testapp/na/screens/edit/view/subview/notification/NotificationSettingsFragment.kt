package example.testapp.na.screens.edit.view.subview.notification

import android.annotation.SuppressLint
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import butterknife.BindView
import butterknife.OnClick
import example.testapp.na.R
import example.testapp.na.vipercore.view.BaseFragment
import example.testapp.na.core.managers.app.DateTimePickerHelper
import example.testapp.na.core.managers.support.ISharedCalendar
import example.testapp.na.screens.edit.interactor.IEditInteractor
import example.testapp.na.screens.edit.presenter.IEditPresenter
import example.testapp.na.screens.edit.router.IEditRouter
import example.testapp.na.screens.edit.view.IEditView
import example.testapp.na.tools.credentials.Types
import rc.extensions.workers.Workers
import javax.inject.Inject

@SuppressLint("ValidFragment")
class NotificationSettingsFragment(private val parent: INotificationSheet) :
        BaseFragment(R.layout.fragment_notification_settings),
        INotificationSettingsFragment {

    @Inject
    lateinit var presenter: IEditPresenter<IEditView, IEditInteractor, IEditRouter>

    @Inject
    lateinit var picker: DateTimePickerHelper

    @Inject
    lateinit var sharedCalendar: ISharedCalendar

    @BindView(R.id.notificationDateText)
    lateinit var nDateText: TextView

    @BindView(R.id.notificationTimeText)
    lateinit var nTimeText: TextView

    @BindView(R.id.typeAlarm)
    lateinit var typeAlarm: ImageView

    @BindView(R.id.typeNotification)
    lateinit var typeNotification: ImageView

    @BindView(R.id.typeNoSignal)
    lateinit var typeNoSignal: ImageView

    override fun onFragmentCreated(view: View) {
        provideComponent().inject(this)
        attachListeners()
        initFields()
    }

    override fun attachListeners() {
        sharedCalendar.apply {
            attachTimeListener { updateTime(it) }
            attachDateListener { updateDate(it) }
        }
    }

    override fun initFields() {
        sharedCalendar.apply {
            if (getMillis() != 0L) {
                nDateText.text = getStringDate()
                nTimeText.text = getStringTime()
            }
            presenter.checkoutReminderType { t ->
                changeTypeSelection(
                        when (t) {
                            Types.NOTE_TYPE_NOTIFICATION -> R.id.typeNotification
                            Types.NOTE_TYPE_ALARM -> R.id.typeAlarm
                            else -> R.id.typeNotification
                        }
                )
            }
        }
    }

    @OnClick(R.id.notificationDate, R.id.notificationTime, R.id.notificationRepeating)
    override fun onControlsClicked(src: View) {
        when (src.id) {
            R.id.notificationDate -> picker.startDatePicker(activity?.fragmentManager!!,
                    presenter.onDateSet())
            R.id.notificationTime -> picker.startTimePicker(activity?.fragmentManager!!,
                    presenter.onTimeSet())
            R.id.notificationRepeating -> {
                parent.repeatingSettings()
            }
        }
    }

    @OnClick(R.id.typeNotification, R.id.typeAlarm)
    override fun onTypeSliderClicked(src: View) {
        presenter.changeRemindType(src.id, parent.provideRoot()!!)
                .doOnComplete {
                    changeTypeSelection(src.id)
                }
                .completeOn(Workers.ui())
                .execute()
    }

    @OnClick(R.id.typeNoSignal)
    override fun onDismissReminder(src: View) {
        presenter.onDismissRemind {
            parent.apply {
                dismissNoUpdate()
                hide()
            }
        }
    }

    override fun changeTypeSelection(which: Int) {
        typeNotification.setBackgroundDrawable(resources.getDrawable(if (which == R.id.typeNotification) {
            R.drawable.add_notification_bottom_sheet_active_picker_bg
        } else {
            R.drawable.add_notification_bottom_sheet_no_active_picker_bg
        }))
        typeAlarm.setBackgroundDrawable(resources.getDrawable(if (which == R.id.typeAlarm) {
            R.drawable.add_notification_bottom_sheet_active_picker_bg
        } else {
            R.drawable.add_notification_bottom_sheet_no_active_picker_bg
        }))
    }

    override fun updateTime(time: String) {
        nTimeText.text = time
    }

    override fun updateDate(date: String) {
        nDateText.text = date
    }
}