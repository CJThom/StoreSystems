package com.gpcasiapac.storesystems.feature.collect.api

import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber

sealed interface CollectOutcome {
    data class OrderSelected(val invoiceNumber: InvoiceNumber) : CollectOutcome
    data object Back : CollectOutcome
    data object Logout : CollectOutcome
    //TODO Change to offset.
    data class SignatureSaved(val strokes: List<List<Pair<Double, Double>>>) : CollectOutcome
    data class SignatureRequested(val customerName: String) : CollectOutcome
    data class WorkOrderItemSelected(val invoiceNumber: InvoiceNumber) : CollectOutcome
    data object OpenOrderFulfilment : CollectOutcome
    data object OpenHistory : CollectOutcome
}