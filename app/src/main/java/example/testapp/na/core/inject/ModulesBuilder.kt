package example.testapp.na.core.inject

import example.testapp.na.vipercore.view.ActivityView
import example.testapp.na.core.inject.component.DaggerSupportComponent
import example.testapp.na.core.inject.component.SupportComponent
import example.testapp.na.screens.edit.EditModule
import example.testapp.na.screens.edit.view.IEditView
import example.testapp.na.screens.main.MainModule
import example.testapp.na.screens.main.view.IMainView

object ModulesBuilder {

    fun build(scope: ActivityView, builder: DaggerSupportComponent.Builder):
            SupportComponent {
        when (scope) {
            is IEditView -> builder.editModule(EditModule())
            is IMainView -> builder.mainModule(MainModule())
        }
        return builder.build()
    }
}