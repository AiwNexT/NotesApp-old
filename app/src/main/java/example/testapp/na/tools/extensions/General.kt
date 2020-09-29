package example.testapp.na.tools.extensions

fun e(toDo: () -> Unit) {
    try {
        toDo()
    } catch (ex: Exception) {}
}

fun e(toDo: () -> Unit, onEx: () -> Unit) {
    try {
        toDo()
    } catch (ex: Exception) {
        onEx()
    }
}

inline fun <reified R> kotlin.Boolean.yes(block: (Boolean) -> R): R {
    return if (this) block(this) else this as R
}

inline fun <reified R> kotlin.Boolean.no(block: (Boolean) -> R): R {
    return if (!this) block(this) else this as R
}