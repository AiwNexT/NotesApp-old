package example.testapp.na.core.managers.notifications

import android.content.Context
import android.widget.RemoteViews

interface NotificationContentHelper {

    fun setUpContentLayout(context: Context, root: RemoteViews, lId: Int, content: ArrayList<Int>)
}