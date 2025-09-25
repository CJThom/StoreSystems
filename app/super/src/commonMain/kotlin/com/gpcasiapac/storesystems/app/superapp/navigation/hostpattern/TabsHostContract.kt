package com.gpcasiapac.storesystems.app.superapp.navigation.hostpattern

import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewState
import com.gpcasiapac.storesystems.app.superapp.navigation.TabItem
import com.gpcasiapac.storesystems.feature.collect.api.CollectExternalOutcome

object TabsHostContract {
    sealed interface Event : ViewEvent {
        data class SelectTab(val tab: TabItem) : Event
        data class FromCollect(val outcome: CollectExternalOutcome) : Event
    }

    data class State(
        val tabList: List<TabItem> = listOf(TabItem.Picking(), TabItem.Collect(), TabItem.History()),
        val selectedTab: TabItem = TabItem.Picking()
    ) : ViewState
}
