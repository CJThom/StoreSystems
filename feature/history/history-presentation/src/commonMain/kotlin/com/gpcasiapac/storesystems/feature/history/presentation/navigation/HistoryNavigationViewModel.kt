package com.gpcasiapac.storesystems.feature.history.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.gpcasiapac.storesystems.common.presentation.navigation.BaseNavViewModel

import com.gpcasiapac.storesystems.feature.history.api.HistoryFeatureDestination
import com.gpcasiapac.storesystems.feature.history.api.HistoryOutcome

class HistoryNavigationViewModel :
    BaseNavViewModel<HistoryNavigationContract.Event, HistoryFeatureDestination, HistoryNavigationContract.State>() {

    override fun provideStartKey(): HistoryFeatureDestination = HistoryFeatureDestination.History
    
    override fun createStateWithStack(stack: List<NavKey>): HistoryNavigationContract.State = 
        HistoryNavigationContract.State(stack = stack)

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