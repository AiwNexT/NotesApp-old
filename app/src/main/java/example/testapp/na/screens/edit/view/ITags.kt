package example.testapp.na.screens.edit.view

import example.testapp.na.vipercore.view.AdapterView

interface ITags: AdapterView {

    fun attachParentView(view: IEditView)
}