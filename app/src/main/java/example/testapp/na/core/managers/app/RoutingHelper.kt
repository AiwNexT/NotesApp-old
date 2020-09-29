package example.testapp.na.core.managers.app

import android.app.Activity
import android.content.Context
import android.os.Bundle
import kotlin.reflect.KClass

interface RoutingHelper {

    fun<A: Activity> startActivity(ctx: Context, screen: KClass<A>, bundle: Bundle? = null, flags: Int = 0)
}