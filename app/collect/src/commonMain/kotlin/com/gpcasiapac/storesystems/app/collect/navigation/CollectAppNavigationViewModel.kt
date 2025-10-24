package com.gpcasiapac.storesystems.app.collect.navigation

import com.gpcasiapac.storesystems.common.presentation.navigation.BaseNavViewModel
import com.gpcasiapac.storesystems.feature.collect.api.CollectExternalOutcome
import com.gpcasiapac.storesystems.feature.history.api.HistoryExternalOutcome
import com.gpcasiapac.storesystems.feature.login.api.LoginExternalOutcome

class CollectAppNavigationViewModel :
    BaseNavViewModel<CollectAppNavContract.Event, CollectAppNavContract.State, CollectAppNavContract.Destination>() {

    override fun setInitialState(): CollectAppNavContract.State {
        return CollectAppNavContract.State(stack = listOf(CollectAppNavContract.Destination.LoginHost))
    }

    override fun onStart() {

    }

    override fun handleEvents(event: CollectAppNavContract.Event) {
        when (event) {
            is CollectAppNavContract.Event.FromLogin -> handleLoginExternalOutcome(event.outcome)
            is CollectAppNavContract.Event.FromCollect -> handleExternalCollectOutcome(event.outcome)
            is CollectAppNavContract.Event.FromHistory -> handleHistoryExternalOutcome(event.outcome)
            is CollectAppNavContract.Event.PopBack -> pop()
        }
    }

    private fun handleLoginExternalOutcome(externalOutcome: LoginExternalOutcome) {
        when (externalOutcome) {
            is LoginExternalOutcome.LoginCompleted -> replaceTop(CollectAppNavContract.Destination.CollectHost)
        }
    }

    private fun handleExternalCollectOutcome(externalOutcome: CollectExternalOutcome) {
        when (externalOutcome) {
            is CollectExternalOutcome.Logout -> replaceTop(CollectAppNavContract.Destination.LoginHost)
            is CollectExternalOutcome.OpenScanner -> {
                // TODO: Handle scanner if needed
            }
            is CollectExternalOutcome.OpenHistory -> {
                push(CollectAppNavContract.Destination.HistoryHost)
            }
        }
    }

    private fun handleHistoryExternalOutcome(externalOutcome: HistoryExternalOutcome) {
        // Currently no external outcomes from history feature
        // Add handling here if history feature emits external outcomes in the future
    }
}