package example.testapp.na.vipercore.view

import example.testapp.na.core.inject.component.SupportComponent

interface DialogView: View {

    fun onDialogCreated(view: android.view.View)

    fun hide()

    fun provideComponent(): SupportComponent
}