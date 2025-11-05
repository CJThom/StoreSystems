package com.gpcasiapac.storesystems.feature.history.presentation.navigation

import com.gpcasiapac.storesystems.common.presentation.navigation.BaseNavViewModel
import com.gpcasiapac.storesystems.feature.history.api.HistoryFeatureDestination
import com.gpcasiapac.storesystems.feature.history.api.HistoryOutcome
import com.gpcasiapac.storesystems.feature.history.api.HistoryExternalOutcome

class HistoryNavigationViewModel :
    BaseNavViewModel<HistoryNavigationContract.Event,HistoryNavigationContract.State, HistoryFeatureDestination>() {

    override fun setInitialState(): HistoryNavigationContract.State {
        return HistoryNavigationContract.State(listOf(HistoryFeatureDestination.History))
    }

    override fun onStart() {

    }

    override fun handleEvents(event: HistoryNavigationContract.Event) {
        when (event) {
            is HistoryNavigationContract.Event.Outcome -> handleOutcome(event.outcome)
            is HistoryNavigationContract.Event.PopBack -> handlePop(event.count)
            is HistoryNavigationContract.Event.Push -> push(event.key)
        }
    }

    private fun handlePop(count: Int) {
        val currentSize = viewState.value.stack.size
        // If at root or popping would reach below root, request host to close the feature
        if (currentSize <= 1 || currentSize - count < 1) {
            setEffect { HistoryNavigationContract.Effect.ExternalOutcome(HistoryExternalOutcome.Exit) }
        } else {
            pop(count)
        }
    }

    private fun handleOutcome(outcome: HistoryOutcome) {
        when (outcome) {
            is HistoryOutcome.Back -> handlePop(1)
            is HistoryOutcome.OpenDetails -> push(
                HistoryFeatureDestination.HistoryDetails(
                    type = outcome.type,
                    id = outcome.id
                )
            )
        }
    }
}