package example.testapp.na.data.entities.notes.notescontent

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ContentTags(
        @Expose @SerializedName("tags") var tags: ArrayList<String>
): NoteContent