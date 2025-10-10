package com.gpcasiapac.storesystems.feature.collect.presentation.navigation

import com.gpcasiapac.storesystems.common.presentation.navigation.BaseNavViewModel
import com.gpcasiapac.storesystems.feature.collect.api.CollectExternalOutcome
import com.gpcasiapac.storesystems.feature.collect.api.CollectFeatureDestination
import com.gpcasiapac.storesystems.feature.collect.api.CollectOutcome
import com.gpcasiapac.storesystems.feature.collect.presentation.navigation.CollectNavigationContract.Effect.*

class CollectNavigationViewModel :
    BaseNavViewModel<CollectNavigationContract.Event, CollectNavigationContract.State, CollectFeatureDestination>() {

    override fun setInitialState(): CollectNavigationContract.State {
        return CollectNavigationContract.State(
            stack = listOf(CollectFeatureDestination.Orders),
            mayBeString = null
        )
    }

    override fun onStart() {

    }

    override fun handleEvents(event: CollectNavigationContract.Event) {
        when (event) {
            is CollectNavigationContract.Event.Outcome -> handleOutcome(event.outcome)
            is CollectNavigationContract.Event.PopBack -> pop()
        }
    }

    private fun handleOutcome(outcome: CollectOutcome) {
        when (outcome) {
            is CollectOutcome.OrderSelected -> pushOrReplaceTop(CollectFeatureDestination.OrderFulfilment)
            is CollectOutcome.Back -> pop()
            is CollectOutcome.Logout -> {
                // Emit external outcome to navigate to login screen
                setEffect { ExternalOutcome(CollectExternalOutcome.Logout) }
            }
            is CollectOutcome.SignatureRequested -> push(CollectFeatureDestination.Signature)
            is CollectOutcome.SignatureSaved -> {
                pop()
            }

            is CollectOutcome.NavigateToOrderDetails -> {
                pushOrReplaceTop(CollectFeatureDestination.OrderDetails(outcome.invoiceNumber))
            }
        }
    }
}
