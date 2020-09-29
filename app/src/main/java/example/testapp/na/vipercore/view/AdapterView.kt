package example.testapp.na.vipercore.view

interface AdapterView: View {

    override val viewID: Int
        get() = 0

    fun dataChange()
    fun itemInserted(position: Int)
    fun itemsRangeInserted(start: Int, amount: Int)
    fun itemUpdated(position: Int)
}