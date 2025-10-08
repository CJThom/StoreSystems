package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderLineItem
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.model.CollectOrderLineItemState


internal fun CollectOrderLineItem.toState(): CollectOrderLineItemState {
    return CollectOrderLineItemState(
        lineNumber = this.lineNumber,
        sku = this.sku,
        productNumber = this.productNumber,
        productDescription = this.productDescription,
        quantity = this.quantity,
        unitPrice = this.unitPrice
    )
}

internal fun List<CollectOrderLineItem>.toState(): List<CollectOrderLineItemState> {
    return map { it.toState() }
}
