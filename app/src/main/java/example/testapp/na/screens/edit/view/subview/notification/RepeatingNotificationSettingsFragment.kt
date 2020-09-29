package example.testapp.na.screens.edit.view.subview.notification

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.support.v4.app.FragmentPagerAdapter
import android.support.v4.view.ViewPager
import android.view.View
import android.widget.Button
import butterknife.BindView
import butterknife.BindViews
import butterknife.OnClick
import example.testapp.na.R
import example.testapp.na.tools.credentials.Messages
import example.testapp.na.tools.custom.views.StablePager
import example.testapp.na.vipercore.view.BaseFragment

@SuppressLint("ValidFragment")
class RepeatingNotificationSettingsFragment(private val parent: INotificationSheet):
        BaseFragment(R.layout.fragment_notification_repeating_settings),
        IRepeatingNotificationSettingsFragment {

    @BindView(R.id.repeatingPager) lateinit var repeatingPager: StablePager

    @BindViews(R.id.swithToWeekdays, R.id.switchToCustom) lateinit var tabs: Array<Button>

    override fun onFragmentCreated(view: View) {
        provideComponent().inject(this)
        initPager()
    }

    override fun initPager() {
        repeatingPager.adapter = RepeatingAdapter()
        repeatingPager.setOnPageChangeListener(object : ViewPager.OnPageChangeListener {
            override fun onPageSelected(position: Int) {
                changePageTabState(position)
            }
            override fun onPageScrolled(position: Int, positionOffset: Float, positionOffsetPixels: Int) {}
            override fun onPageScrollStateChanged(state: Int) {}
        })
        repeatingPager.adapter?.notifyDataSetChanged()
    }

    override fun changePageTabState(position: Int) {
        if (position == 0) {
            changeTab(Messages.TITLE_WEEKDAYS, resources.getDrawable(R.drawable.picked_reminder),
                    resources.getColor(android.R.color.white), 0)
            changeTab(Messages.TITLE_CUSTOM, resources.getDrawable(R.drawable.unpicked_reminder),
                    resources.getColor(R.color.colorTextGray), 1)
        } else if (position == 1) {
            changeTab(Messages.TITLE_WEEKDAYS, resources.getDrawable(R.drawable.unpicked_reminder),
                    resources.getColor(R.color.colorTextGray), 0)
            changeTab(Messages.TITLE_CUSTOM, resources.getDrawable(R.drawable.picked_reminder),
                    resources.getColor(android.R.color.white), 1)
        }
    }

    @OnClick(R.id.switchToCustom, R.id.swithToWeekdays)
    override fun tabsClick(v: View) {
        when (v.id) {
            R.id.switchToCustom -> repeatingPager.setCurrentItem(1, true)
            R.id.swithToWeekdays -> repeatingPager.setCurrentItem(0, true)
        }
    }

    private fun changeTab(title: String, bg: Drawable, color: Int, position: Int) {
        tabs[position].apply {
            text = title
            setBackgroundDrawable(bg)
            setTextColor(color)
        }
    }

    inner class RepeatingAdapter(): FragmentPagerAdapter(childFragmentManager) {

        private val fragments = arrayListOf(WeekdaysRepeatingFragment(), CustomRepeatingFragment())

        override fun getCount() = 2

        override fun getItem(position: Int) = fragments[position]
    }
}