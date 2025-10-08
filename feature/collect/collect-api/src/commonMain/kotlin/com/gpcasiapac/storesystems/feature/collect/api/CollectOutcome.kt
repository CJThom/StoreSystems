package com.gpcasiapac.storesystems.feature.collect.api

sealed interface CollectOutcome {
    data class OrderSelected(val orderId: String) : CollectOutcome
    data object Back : CollectOutcome
    data object Logout : CollectOutcome
    //TODO Change to offset.
    data class SignatureSaved(val strokes: List<List<Pair<Double, Double>>>) : CollectOutcome
    data object SignatureRequested : CollectOutcome
    data class NavigateToOrderDetails(val invoiceNumber: String) : CollectOutcome
}