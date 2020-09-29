package example.testapp.na.vipercore.presenter

import example.testapp.na.vipercore.router.Router
import example.testapp.na.vipercore.view.View
import example.testapp.na.vipercore.interactor.Interactor

interface Presenter<V: View, I: Interactor, R: Router> {

    fun onAttach(view: V?)
    fun onDetach()

    fun view(): V?

    fun setUpRouter(view: V?)
}