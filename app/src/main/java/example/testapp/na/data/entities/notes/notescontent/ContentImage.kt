package example.testapp.na.data.entities.notes.notescontent

import android.graphics.Bitmap
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ContentImage(@Expose @SerializedName("image64") var base64: String, var image: Bitmap): NoteContent