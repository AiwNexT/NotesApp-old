package example.testapp.na.tools.extensions

import android.graphics.Bitmap
import example.testapp.na.tools.utils.BitmapUtils

fun Bitmap.compress(): Bitmap {
    return BitmapUtils.compress(this)
}

fun Bitmap.toBase64(): String {
    return BitmapUtils.encodeToBase64(this)
}

fun Any.fromBase64(base64: String): Bitmap {
    return BitmapUtils.decodeFromBase64(base64)
}
