package example.testapp.na.vipercore.view

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import butterknife.ButterKnife
import example.testapp.na.core.inject.component.SupportComponent

abstract class BaseDialog(override val viewID: Int) : DialogView, DialogFragment() {

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return inflater.inflate(viewID, container, false).apply {
            bind(this)
            onDialogCreated(this)
        }
    }

    private fun bind(view: View) {
        ButterKnife.bind(this, view)
    }

    override fun hide() {
        dismiss()
    }

    override fun provideComponent(): SupportComponent {
        return (activity as ActivityView).supportComponent()
    }
}