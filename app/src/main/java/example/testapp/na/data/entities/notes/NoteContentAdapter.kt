package example.testapp.na.data.entities.notes

import com.google.gson.*
import example.testapp.na.data.entities.notes.notescontent.NoteContent
import java.lang.reflect.Type
import com.google.gson.JsonParseException
import com.google.gson.JsonObject

class NoteContentAdapter : JsonSerializer<NoteContent>, JsonDeserializer<NoteContent> {

    companion object {
        private const val CLASSNAME = "CLASSNAME"
        private const val CONTENT = "CONTENT"
    }

    override fun serialize(src: NoteContent?, typeOfSrc: Type?, context: JsonSerializationContext?): JsonElement {
        val jsonObject = JsonObject()
        jsonObject.addProperty(CLASSNAME, src!!::class.java.name)
        jsonObject.add(CONTENT, context?.serialize(src))
        return jsonObject
    }

    override fun deserialize(json: JsonElement?, typeOfT: Type?, context: JsonDeserializationContext?): NoteContent {
        val jsonObject = json?.asJsonObject
        val prim = jsonObject?.get(CLASSNAME) as JsonPrimitive
        val className = prim.asString
        val klass = getObjectClass(className)
        return context?.deserialize(jsonObject.get(CONTENT), klass)!!
    }

    private fun getObjectClass(className: String): Class<*> {
        try {
            return Class.forName(className)
        } catch (e: ClassNotFoundException) {
            throw JsonParseException(e.message)
        }
    }
}