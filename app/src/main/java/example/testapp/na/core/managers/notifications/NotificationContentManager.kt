package example.testapp.na.core.managers.notifications

import android.content.Context
import android.widget.RemoteViews
import example.testapp.na.R
import javax.inject.Inject

class NotificationContentManager @Inject constructor(): NotificationContentHelper {

    override fun setUpContentLayout(context: Context, root: RemoteViews, lId: Int, content: ArrayList<Int>) {
        content.forEach {
            val views = RemoteViews(context.packageName, R.layout.notification_content_type)
            views.setImageViewResource(R.id.contentImage, it)
            root.addView(lId, views)
        }
    }
}