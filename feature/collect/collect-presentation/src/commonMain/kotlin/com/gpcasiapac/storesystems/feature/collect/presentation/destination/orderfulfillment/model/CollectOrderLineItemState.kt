package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.model

import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderLineItem

@Immutable
data class CollectOrderLineItemState(
    val lineNumber: Int,
    val sku: String,
    val description: String,
    val quantity: Int,
    val imageUrl: String? = null,
) {

    companion object {

        internal fun placeholder(index: Int = 0): CollectOrderLineItemState =
            CollectOrderLineItemState(
                lineNumber = index,
                sku = "SKU-00$index",
                description = "Product $index Description",
                quantity = index,
            )

        internal fun placeholderList(count: Int = 6): List<CollectOrderLineItemState> =
            List(count) { index -> placeholder(index) }

    }
}

internal fun CollectOrderLineItem.toState(): CollectOrderLineItemState {
    return CollectOrderLineItemState(
        lineNumber = lineNumber,
        sku = sku,
        description = description,
        quantity = quantity,
        imageUrl = imageUrl
    )
}
