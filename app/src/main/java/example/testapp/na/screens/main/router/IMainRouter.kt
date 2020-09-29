package example.testapp.na.screens.main.router

import android.os.Bundle
import example.testapp.na.vipercore.router.Router

interface IMainRouter: Router {

    fun startEditorActivity(config: Bundle?)
}