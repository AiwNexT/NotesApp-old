package example.testapp.na.data.entities.notes.notescontent

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import example.testapp.na.tools.credentials.Priorities

data class ContentPriority(
        @Expose @SerializedName("priority") var priority: Int = Priorities.MEDIUM
): NoteContent