package example.testapp.na.tools.extensions

import android.view.View
import example.testapp.na.tools.utils.SizeUtils

fun View.dpToPx(height: Int): Int {
    return SizeUtils.dpToPx(height, context)
}