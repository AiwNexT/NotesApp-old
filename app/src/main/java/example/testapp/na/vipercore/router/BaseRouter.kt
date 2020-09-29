package example.testapp.na.vipercore.router

import android.content.Context
import example.testapp.na.core.managers.app.RoutingHelper

abstract class BaseRouter internal constructor(var routingHelper: RoutingHelper): Router {

    override var routingContext: Context? = null
}