package com.gpcasiapac.storesystems.feature.collect.presentation.navigation

import com.gpcasiapac.storesystems.common.presentation.navigation.BaseNavViewModel

class CollectNavigationViewModel :
    BaseNavViewModel<CollectNavContract.Event, CollectStep>() {

    override fun provideStartKey(): CollectStep = CollectStep.Orders

    override fun handleEvents(event: CollectNavContract.Event) {
        when (event) {
            is CollectNavContract.Event.ToOrderDetails -> push(CollectStep.OrderDetails(event.orderId))
            is CollectNavContract.Event.PopBack -> pop(event.count)
        }
    }

}
