package example.testapp.na.screens.edit

import dagger.Module
import dagger.Provides
import example.testapp.na.core.inject.scopes.Support
import example.testapp.na.screens.edit.interactor.EditInteractor
import example.testapp.na.screens.edit.interactor.IEditInteractor
import example.testapp.na.screens.edit.presenter.EditPresenter
import example.testapp.na.screens.edit.presenter.IEditPresenter
import example.testapp.na.screens.edit.router.EditRouter
import example.testapp.na.screens.edit.router.IEditRouter
import example.testapp.na.screens.edit.view.IEditView

@Module
class EditModule {

    @Support
    @Provides
    fun provideInteractor(interactor: EditInteractor): IEditInteractor = interactor

    @Support
    @Provides
    fun provideRouter(router: EditRouter): IEditRouter = router

    @Support
    @Provides
    fun providePresenter(presenter: EditPresenter<IEditView, IEditInteractor, IEditRouter>):
            IEditPresenter<IEditView, IEditInteractor, IEditRouter> = presenter
}