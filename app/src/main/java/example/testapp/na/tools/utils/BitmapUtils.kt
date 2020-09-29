package example.testapp.na.tools.utils

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import java.io.ByteArrayOutputStream

object BitmapUtils {

    private const val MAX_BITMAP_SIZE = 640

    fun encodeToBase64(bitmap: Bitmap): String {
        val byteArrayOutputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
        val byteArray = byteArrayOutputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }

    fun decodeFromBase64(toDecode: String): Bitmap {
        val decodedString = Base64.decode(toDecode, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    fun compress(image: Bitmap): Bitmap {
        var width = image.width
        var height = image.height

        return if ((width > MAX_BITMAP_SIZE) or (height > MAX_BITMAP_SIZE)) {
            val bitmapRatio = width.toFloat() / height.toFloat()
            if (bitmapRatio > 1) {
                width = MAX_BITMAP_SIZE
                height = (width / bitmapRatio).toInt()
            } else {
                height = MAX_BITMAP_SIZE
                width = (height * bitmapRatio).toInt()
            }

            Bitmap.createScaledBitmap(image, width, height, true)
        } else {
            image
        }
    }
}