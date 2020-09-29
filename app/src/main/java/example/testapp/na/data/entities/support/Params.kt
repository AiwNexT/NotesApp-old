package example.testapp.na.data.entities.support

import io.realm.RealmList
import io.realm.RealmObject
import io.realm.annotations.Ignore

open class Params(): RealmObject() {

    var type: Int = 0
    var timeMillis: Long = 0
    var delayedMillis: Long = 0
    var locked: Boolean = false
    var pinned: Boolean = false
    var time: String = ""
    var date: String = ""
    var weekDays: RealmList<Int>? = null
    var repeatingInterval: Long = 0L
    var repeatingPeriod: Long = 0L
    @Ignore var repeatingIntervalPair: Pair<Int, String>? = null

    constructor(type: Int, timeMillis: Long, delayedMillis: Long, locked: Boolean,
                pinned: Boolean, time: String, date: String,
                weekDays: RealmList<Int>?, repeatingInterval: Long,
                repeatingPeriod: Long, repeatingIntervalPair: Pair<Int, String>) : this() {
        this.type = type
        this.timeMillis = timeMillis
        this.delayedMillis = delayedMillis
        this.locked = locked
        this.pinned = pinned
        this.time = time
        this.date = date
        this.weekDays = weekDays
        this.repeatingInterval = repeatingInterval
        this.repeatingPeriod = repeatingPeriod
        this.repeatingIntervalPair = repeatingIntervalPair
    }
}


