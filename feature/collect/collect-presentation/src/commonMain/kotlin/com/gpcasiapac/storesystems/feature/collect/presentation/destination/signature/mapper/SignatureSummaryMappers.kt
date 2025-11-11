package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.mapper

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.model.SignatureSummaryState

/**
 * Direct domain → SignatureSummaryState mapper for efficiency in ViewModel.
 */
fun List<CollectOrderWithCustomerWithLineItems>.toSignatureSummary(): SignatureSummaryState {
    return if (size == 1) {
        val single = first()
        val totalQuantity = single.lineItemList.sumOf { it.quantity }
        SignatureSummaryState.Single(
            invoiceNumber = single.order.invoiceNumber.value,
            customerName = single.customer.name,
            totalQuantity = totalQuantity
        )
    } else {
        val invoiceNumberList: List<String> = map { it.order.invoiceNumber.value }
        val totalQuantity = sumOf { domain -> domain.lineItemList.sumOf { it.quantity } }
        SignatureSummaryState.Multi(
            invoiceNumberList = invoiceNumberList,
            totalQuantity = totalQuantity
        )
    }
}
