package example.testapp.na.screens.edit.router

import android.app.Activity
import android.content.Intent
import example.testapp.na.vipercore.router.BaseRouter
import example.testapp.na.core.managers.app.RoutingHelper
import example.testapp.na.tools.credentials.Messages
import example.testapp.na.tools.credentials.Requests
import javax.inject.Inject

class EditRouter @Inject internal constructor(routingHelper: RoutingHelper):
        BaseRouter(routingHelper = routingHelper), IEditRouter {

    override fun startImagePicker() {
        Intent().apply {
            type = "image/*"
            action = Intent.ACTION_GET_CONTENT
            (routingContext as Activity)
                    .startActivityForResult(Intent.createChooser(this, Messages.SLECT_IMAGE), Requests.PICK_IMAGE)
        }
    }

    override fun backToMain() {
        (routingContext as Activity).finish()
    }
}