package example.testapp.na.data.entities.notes.notescontent

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import example.testapp.na.data.entities.support.ListItem

data class ContentList(
        @Expose @SerializedName("list") var items: ArrayList<ListItem>
): NoteContent