package example.testapp.na.vipercore.view

import example.testapp.na.core.inject.component.SupportComponent

interface ActivityView: View {

    fun supportComponent(): SupportComponent

    fun buildComponent(): SupportComponent
}