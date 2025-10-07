package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderLineItem
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail.model.CollectOrderLineItemState


internal fun CollectOrderLineItem.toState(): CollectOrderLineItemState {
    return CollectOrderLineItemState(
        lineNumber = this.lineNumber
    )
}

internal fun List<CollectOrderLineItem>.toState(): List<CollectOrderLineItemState> {
    return map { it.toState() }
}