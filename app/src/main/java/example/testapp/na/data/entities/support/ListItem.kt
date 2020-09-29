package example.testapp.na.data.entities.support

import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import io.realm.RealmModel

data class ListItem(@Expose @SerializedName("item") var item: String,
                    @Expose @SerializedName("checked") var checked: Boolean): RealmModel