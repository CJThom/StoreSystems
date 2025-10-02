package com.gpcasiapac.storesystems.app.collect.navigation.hostpattern

import com.gpcasiapac.storesystems.common.presentation.navigation.BaseNavViewModel
import com.gpcasiapac.storesystems.feature.collect.api.CollectExternalOutcome
import com.gpcasiapac.storesystems.feature.login.api.LoginExternalOutcome

class CollectAppNavigationViewModel :
    BaseNavViewModel<CollectAppNavContract.Event, CollectAppDestination>() {

    override fun provideStartKey(): CollectAppDestination = CollectAppDestination.LoginHost

    override fun handleEvents(event: CollectAppNavContract.Event) {
        when (event) {
            is CollectAppNavContract.Event.FromLogin -> handleLoginExternalOutcome(event.outcome)
            is CollectAppNavContract.Event.FromCollect -> handleExternalCollectOutcome(event.outcome)
            is CollectAppNavContract.Event.PopBack -> pop(event.count)
        }
    }

    private fun handleLoginExternalOutcome(externalOutcome: LoginExternalOutcome) {
        when (externalOutcome) {
            is LoginExternalOutcome.LoginCompleted -> replaceTop(CollectAppDestination.CollectHost)
        }
    }

    private fun handleExternalCollectOutcome(externalOutcome: CollectExternalOutcome) {
        when (externalOutcome) {
            is CollectExternalOutcome.Logout -> replaceTop(CollectAppDestination.LoginHost)
            is CollectExternalOutcome.OpenScanner -> {
                // TODO: Handle scanner if needed
            }
        }
    }
}