package example.testapp.na.tools.custom.groups

import example.testapp.na.screens.main.view.IMainView

interface IFabMenu {

    var currentState: Boolean

    fun attach(parent: IMainView, menu: android.view.View)

    fun showMenu()
    fun hideMenu()
}