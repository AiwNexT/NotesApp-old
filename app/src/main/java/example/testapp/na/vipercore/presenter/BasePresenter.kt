package example.testapp.na.vipercore.presenter

import example.testapp.na.vipercore.interactor.Interactor
import example.testapp.na.vipercore.router.Router
import example.testapp.na.vipercore.view.View

abstract class BasePresenter<V: View, I: Interactor, R: Router> internal constructor(var interactor: I, var router: R): Presenter<V, I, R> {

    var view: V? = null

    override fun onAttach(view: V?) {
        this.view = view
        setUpRouter(view)
    }

    override fun onDetach() {
        view = null
    }

    override fun view(): V? = view

    override fun setUpRouter(view: V?) {
        router.routingContext = view?.routingContext()
    }
}