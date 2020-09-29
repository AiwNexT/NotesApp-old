package example.testapp.na.data.entities.notes

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.annotations.Expose
import com.google.gson.annotations.SerializedName
import example.testapp.na.data.entities.notes.notescontent.*
import example.testapp.na.data.entities.support.Params
import io.realm.RealmObject
import io.realm.annotations.Ignore
import io.realm.annotations.PrimaryKey
import com.google.gson.reflect.TypeToken
import example.testapp.na.tools.extensions.fromBase64
import io.realm.RealmList
import java.lang.reflect.Type

open class Note() : RealmObject() {

    @PrimaryKey
    var id: Long = 0
    var archived: Boolean = false
    var removed: Boolean = false
    var params: Params? = null
    private var entitiesList: RealmList<String> = RealmList()
    @Ignore @Expose @SerializedName("entities")
    var entities: ArrayList<NoteContent> = arrayListOf()

    constructor(id: Long, archived: Boolean, removed: Boolean, entities: ArrayList<NoteContent>, params: Params?) : this() {
        this.id = id
        this.archived = archived
        this.removed = removed
        this.entities = entities
        this.params = params
    }

    fun updateJsonObject() {
        entitiesList.clear()
        val entitiesString = gson().toJson(entities, type())
        jsonToRealm(entitiesString)
    }

    fun restoreFromJson() {
        val entitiesString = realmToJson()
        entities = gson().fromJson(entitiesString, type())
        entities.filter { it is ContentImage }.forEach {
            (it as ContentImage)
                    .apply { image = fromBase64(base64) }
        }
    }

    private fun gson(): Gson {
        val builder = GsonBuilder().excludeFieldsWithoutExposeAnnotation().disableHtmlEscaping()
        builder.registerTypeAdapter(NoteContent::class.java, NoteContentAdapter())
        builder.setLenient()
        return builder.create()
    }

    private fun type(): Type {
        return object : TypeToken<ArrayList<NoteContent>>() {}.type
    }

    private fun jsonToRealm(str: String) {
        for (i in 0 until str.length step 16777199) {
            entitiesList.add(str.substring(i, Math.min(i + 16777199, str.length)))
        }
    }

    private fun realmToJson(): String {
        var json = ""
        apply {
            entitiesList.forEach { if (it != null) json += it }
        }
        entitiesList.clear()
        return json
    }
}

