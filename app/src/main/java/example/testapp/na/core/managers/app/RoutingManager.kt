package example.testapp.na.core.managers.app

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import javax.inject.Inject
import kotlin.reflect.KClass

class RoutingManager @Inject constructor() : RoutingHelper {

    override fun<A: Activity> startActivity(ctx: Context, screen: KClass<A>, bundle: Bundle?, flags: Int) {
        ctx.startActivity(Intent(ctx, screen.java).apply {
            setFlags(flags)
            bundle?.apply { putExtras(this) }
        })
    }
}