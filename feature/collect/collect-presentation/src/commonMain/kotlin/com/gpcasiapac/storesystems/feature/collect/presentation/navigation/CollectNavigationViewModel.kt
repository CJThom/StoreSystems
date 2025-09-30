package com.gpcasiapac.storesystems.feature.collect.presentation.navigation

import com.gpcasiapac.storesystems.common.presentation.navigation.BaseNavViewModel
import com.gpcasiapac.storesystems.feature.collect.api.CollectFeatureDestination
import com.gpcasiapac.storesystems.feature.collect.api.CollectOutcome

class CollectNavigationViewModel :
    BaseNavViewModel<CollectNavigationContract.Event, CollectFeatureDestination>() {

    override fun provideStartKey(): CollectFeatureDestination = CollectFeatureDestination.Orders

    override fun handleEvents(event: CollectNavigationContract.Event) {
        when (event) {
            is CollectNavigationContract.Event.Outcome -> handleOutcome(event.outcome)
            is CollectNavigationContract.Event.PopBack -> pop(event.count)
        }
    }

    private fun handleOutcome(outcome: CollectOutcome) {
        // No external outcomes to emit (internal navigation only for now)
        when (outcome) {
            is CollectOutcome.OrderSelected -> push(CollectFeatureDestination.OrderDetails(outcome.orderId))
            is CollectOutcome.Back -> pop()
            is CollectOutcome.SignatureRequested -> push(CollectFeatureDestination.Signature)
            is CollectOutcome.SignatureSaved -> pop()
        }
    }
}
