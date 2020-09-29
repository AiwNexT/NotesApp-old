package example.testapp.na.vipercore.interactor

import example.testapp.na.data.preferences.PreferencesHelper

abstract class BaseInteractor(): Interactor {

    private lateinit var preferencesHelper: PreferencesHelper

    constructor(preferencesHelper: PreferencesHelper): this() {
        this.preferencesHelper = preferencesHelper
    }
}