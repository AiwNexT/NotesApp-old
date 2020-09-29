package example.testapp.na.screens.edit.view.subview.notification

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentPagerAdapter
import android.view.View
import butterknife.BindView
import example.testapp.na.R
import example.testapp.na.vipercore.view.BaseSheet
import example.testapp.na.screens.edit.interactor.IEditInteractor
import example.testapp.na.screens.edit.presenter.IEditPresenter
import example.testapp.na.screens.edit.router.IEditRouter
import example.testapp.na.screens.edit.view.IEditView
import example.testapp.na.tools.custom.views.StablePager
import kotlinx.android.synthetic.main.sheet_notification_settings.view.*
import javax.inject.Inject

@SuppressLint("ValidFragment")
class NotificationSheet :
        BaseSheet(R.layout.sheet_notification_settings),
        INotificationSheet {

    override var updateBeforeDismiss = true

    @BindView(R.id.notificationPager) lateinit var pager: StablePager

    @Inject lateinit var presenter: IEditPresenter<IEditView, IEditInteractor, IEditRouter>

    override fun onDialogCreated(view: View) {
        provideComponent().inject(this)
        bindSlider(view)
    }

    override fun bindSlider(view: View) {
        pager.apply {
            adapter = buildAdapter()
            adapter?.notifyDataSetChanged()
        }
    }

    override fun provideRoot(): View? = view!!.snackView

    override fun mainSettings() = pager.setCurrentItem(0, true)

    override fun repeatingSettings() = pager.setCurrentItem(1, true)

    override fun buildAdapter(): PagerAdapter {
        return PagerAdapter(childFragmentManager)
    }

    inner class PagerAdapter(fm: FragmentManager?) : FragmentPagerAdapter(fm) {

        private val fragments = arrayListOf(
                NotificationSettingsFragment( this@NotificationSheet),
                RepeatingNotificationSettingsFragment(this@NotificationSheet))

        override fun getItem(position: Int) = fragments[position]
        override fun getCount() = fragments.size
    }

    override fun onDismiss(dialog: DialogInterface?) {
        super.onDismiss(dialog)
        updateNoteOnDismiss()
    }

    override fun updateNoteOnDismiss() {
        if (updateBeforeDismiss) presenter.saveTimeDateSettings()
    }

    override fun dismissNoUpdate() {
        updateBeforeDismiss = false
    }
}