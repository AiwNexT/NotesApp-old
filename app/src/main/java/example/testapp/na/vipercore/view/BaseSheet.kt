package example.testapp.na.vipercore.view

import android.os.Bundle
import android.support.design.widget.BottomSheetDialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import example.testapp.na.core.inject.component.SupportComponent

abstract class BaseSheet(override val viewID: Int): BottomSheetDialogFragment(), DialogView {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(viewID, container, false).apply {
            bind(this)
            onDialogCreated(this)
        }
    }

    private fun bind(view: View) {
        ButterKnife.bind(this, view)
    }

    override fun provideComponent(): SupportComponent {
        return (activity as ActivityView).supportComponent()
    }

    override fun hide() {
        dismiss()
    }
}