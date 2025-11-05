package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper


import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrder
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.model.CollectOrderState

internal fun CollectOrder.toState(): CollectOrderState {
    return CollectOrderState(
        invoiceNumber = this.invoiceNumber,
        orderNumber = this.orderNumber,
        webOrderNumber = this.webOrderNumber,
        createdAt = this.createdDateTime,
        pickedAt = this.invoiceDateTime
    )
}

internal fun List<CollectOrder>.toState(): List<CollectOrderState> {
    return map { it.toState() }
}

