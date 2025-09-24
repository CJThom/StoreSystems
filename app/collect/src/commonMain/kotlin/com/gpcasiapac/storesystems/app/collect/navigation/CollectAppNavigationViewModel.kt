package com.gpcasiapac.storesystems.app.collect.navigation

import com.gpcasiapac.storesystems.common.presentation.navigation.BaseNavViewModel

class CollectAppNavigationViewModel :
    BaseNavViewModel<CollectAppNavContract.Event, CollectAppDestination>() {

    override fun provideStartKey(): CollectAppDestination = CollectAppDestination.LoginHost

    override fun handleEvents(event: CollectAppNavContract.Event) {
        when (event) {
            is CollectAppNavContract.Event.ToCollectHost -> push(CollectAppDestination.CollectHost)
            is CollectAppNavContract.Event.PopBack -> pop(event.count)
        }
    }
}