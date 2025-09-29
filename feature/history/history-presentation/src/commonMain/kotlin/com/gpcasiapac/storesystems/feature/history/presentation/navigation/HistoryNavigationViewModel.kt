package com.gpcasiapac.storesystems.feature.history.presentation.navigation

import com.gpcasiapac.storesystems.common.presentation.navigation.BaseNavViewModel
import com.gpcasiapac.storesystems.feature.history.api.HistoryFeatureDestination
import com.gpcasiapac.storesystems.feature.history.api.HistoryOutcome

class HistoryNavigationViewModel :
    BaseNavViewModel<HistoryNavigationContract.Event, HistoryFeatureDestination>() {

    override fun provideStartKey(): HistoryFeatureDestination = HistoryFeatureDestination.History

    override fun handleEvents(event: HistoryNavigationContract.Event) {
        when (event) {
            is HistoryNavigationContract.Event.Outcome -> handleOutcome(event.outcome)
            is HistoryNavigationContract.Event.PopBack -> pop(event.count)
        }
    }

    private fun handleOutcome(outcome: HistoryOutcome) {
        when (outcome) {
            is HistoryOutcome.Back -> pop()
        }
    }
}