package com.gpcasiapac.storesystems.feature.collect.api

sealed interface CollectOutcome {
    data class OrderSelected(val orderId: String) : CollectOutcome
    data object Back : CollectOutcome
}