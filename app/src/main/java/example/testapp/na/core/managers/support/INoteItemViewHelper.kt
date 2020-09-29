package example.testapp.na.core.managers.support

import android.view.View
import example.testapp.na.screens.main.view.INoteContentAdapter
import example.testapp.na.vipercore.view.ActivityView
import rc.extensions.scope.RCScope

interface INoteItemViewHelper: RCScope {

    fun create(v: ActivityView)

    fun update(note: View, noteView: View, additionalHeight: Int,  adapter: INoteContentAdapter)
}