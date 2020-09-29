package example.testapp.na.tools.utils

import android.content.Context
import android.util.DisplayMetrics

object SizeUtils {

    fun dpToPx(dp: Int, context: Context): Int {
        val resources = context.getResources()
        val metrics = context.resources.getDisplayMetrics()
        return (dp * (metrics.densityDpi.toFloat() / DisplayMetrics.DENSITY_DEFAULT)).toInt()
    }
}