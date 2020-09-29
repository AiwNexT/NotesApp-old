package example.testapp.na.data.entities.notes.notescontent

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName

data class ContentNote(
        @Expose @SerializedName("note") var note: String
): NoteContent