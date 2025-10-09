package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper


import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrder
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.model.CollectOrderState

internal fun CollectOrder.toState(): CollectOrderState {
    return CollectOrderState(
        invoiceNumber = this.invoiceNumber,
        salesOrderNumber = this.salesOrderNumber,
        webOrderNumber = this.webOrderNumber,
        createdAt = this.createdAt,
        pickedAt = this.pickedAt,
        signature = this.signature
    )
}

internal fun List<CollectOrder>.toState(): List<CollectOrderState> {
    return map { it.toState() }
}

