package com.gpcasiapac.storesystems.app.superapp.navigation.hostpattern

import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.feature.collect.api.CollectExternalOutcome

class TabsHostViewModel : MVIViewModel<TabsHostContract.Event, TabsHostContract.State, ViewSideEffect>() {

    override fun setInitialState(): TabsHostContract.State = TabsHostContract.State()

    override fun onStart() { /* no-op */ }

    override fun handleEvents(event: TabsHostContract.Event) {
        when (event) {
            is TabsHostContract.Event.SelectTab -> setState { copy(selectedTab = event.tab) }
            is TabsHostContract.Event.FromCollect -> handleCollectExternal(event.outcome)
        }
    }

    private fun handleCollectExternal(outcome: CollectExternalOutcome) {
        when (outcome) {
            // Placeholder example: handle an external Collect request at the tabs level
            is CollectExternalOutcome.OpenScanner -> {
                // Example: you could set an effect to trigger a scanner, or route to a Scanner host
                // setEffect { TabsHostEffect.OpenScanner }
            }
        }
    }
}
