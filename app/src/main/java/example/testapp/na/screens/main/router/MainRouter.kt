package example.testapp.na.screens.main.router

import android.os.Bundle
import example.testapp.na.vipercore.router.BaseRouter
import example.testapp.na.core.managers.app.RoutingHelper
import example.testapp.na.screens.edit.view.EditActivity
import javax.inject.Inject

class MainRouter @Inject internal constructor(routingHelper: RoutingHelper):
        BaseRouter(routingHelper = routingHelper), IMainRouter {

    override fun startEditorActivity(config: Bundle?) {
        routingContext?.apply { routingHelper.startActivity(this, EditActivity::class, config, 0) }
    }
}