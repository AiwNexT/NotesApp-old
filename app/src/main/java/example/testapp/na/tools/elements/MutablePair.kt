package example.testapp.na.tools.elements

data class MutablePair<F: Any?, S: Any?>(
        var first: F? = null,
        var second: S? = null)