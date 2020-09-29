package example.testapp.na.screens.main

import dagger.Module
import dagger.Provides
import example.testapp.na.core.inject.scopes.Support
import example.testapp.na.core.managers.app.SchedulingUpdatesHelper
import example.testapp.na.core.managers.app.SchedulingUpdatesManager
import example.testapp.na.screens.main.interactor.IMainInteractor
import example.testapp.na.screens.main.interactor.MainInteractor
import example.testapp.na.screens.main.presenter.IMainPresenter
import example.testapp.na.screens.main.presenter.MainPresenter
import example.testapp.na.screens.main.router.IMainRouter
import example.testapp.na.screens.main.router.MainRouter
import example.testapp.na.screens.main.view.IMainView
import example.testapp.na.tools.custom.groups.FabMenu
import example.testapp.na.tools.custom.groups.IFabMenu

@Module
class MainModule {

    @Support
    @Provides
    fun provideInteractor(interactor: MainInteractor): IMainInteractor = interactor

    @Support
    @Provides
    fun provideRouter(router: MainRouter): IMainRouter = router

    @Support
    @Provides
    fun providePresenter(presenter: MainPresenter<IMainView, IMainInteractor, IMainRouter>):
            IMainPresenter<IMainView, IMainInteractor, IMainRouter> = presenter

    @Support
    @Provides
    fun provideFabMenu(menu: FabMenu): IFabMenu = menu

    @Support
    @Provides
    fun provideTracker(tracker: SchedulingUpdatesManager): SchedulingUpdatesHelper = tracker
}