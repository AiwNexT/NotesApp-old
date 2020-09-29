package example.testapp.na.vipercore.view

import example.testapp.na.core.inject.component.SupportComponent

interface FragmentView {

    fun onFragmentCreated(view: android.view.View)

    fun provideComponent(): SupportComponent
}