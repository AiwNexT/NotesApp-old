package example.testapp.na.tools.extensions

import com.vicpin.krealmextensions.createOrUpdate
import com.vicpin.krealmextensions.saveAll
import example.testapp.na.data.entities.notes.Note

fun<A: Any> ArrayList<A>.swap(first: Int, second: Int) {
    val temp = this[first]
    this[first] = this[second]
    this[second] = temp
}

fun <A: Any> A.offset(collection: ArrayList<A>): Int {
    return collection.indexOf(this)
}

fun Note.updateAndSave() {
    updateJsonObject()
    createOrUpdate()
}

fun ArrayList<Note>.updateAndSave() {
    forEach { it.updateJsonObject() }
    saveAll()
}

fun Note.restore(): Note {
    restoreFromJson()
    return this
}

fun List<Note>.restore(): List<Note> {
    forEach { it.restoreFromJson() }
    return this
}
