package example.testapp.na.vipercore.view

import android.content.Context
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import butterknife.ButterKnife
import example.testapp.na.R
import example.testapp.na.core.app.IApp
import example.testapp.na.core.inject.ModulesBuilder
import example.testapp.na.core.inject.component.DaggerSupportComponent
import example.testapp.na.core.inject.component.SupportComponent
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper

abstract class BaseActivity(override val viewID: Int): AppCompatActivity(), ActivityView {

    override fun routingContext(): Context = this

    private lateinit var supportComponent: SupportComponent

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(viewID)
        setUpAnimations()
        bind()

        onViewCreated()
    }

    override fun setUpAnimations() {
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
    }

    override fun onBackPressed() {
        overridePendingTransition(R.anim.slide_up, R.anim.slide_down)
        super.onBackPressed()
    }

    override fun onViewCreated() {}

    override fun supportComponent(): SupportComponent {
        return supportComponent
    }

    override fun buildComponent(): SupportComponent {
        supportComponent = ModulesBuilder.build(this, DaggerSupportComponent.builder()
                .appComponent((application as IApp).appComponent))
        return supportComponent
    }

    private fun bind() = ButterKnife.bind(this)

    override fun attachBaseContext(newBase: Context?) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase))
    }
}