package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail.model

import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderLineItem

@Immutable
data class CollectOrderLineItemState(
    val lineNumber: Int,
    val sku: String,
    val productNumber: String,
    val productDescription: String,
    val quantity: Int,
    val unitPrice: Double
) {

    companion object {

        internal fun placeholder(index: Int = 0): CollectOrderLineItemState =
            CollectOrderLineItemState(
                lineNumber = index,
                sku = "SKU-00$index",
                productNumber = "PROD-00$index",
                productDescription = "Product $index Description",
                quantity = index,
                unitPrice = index * 10.0
            )

        internal fun placeholderList(count: Int = 6): List<CollectOrderLineItemState> =
            List(count) { index -> placeholder(index) }

    }
}

internal fun CollectOrderLineItem.toState(): CollectOrderLineItemState {
    return CollectOrderLineItemState(
        lineNumber = lineNumber,
        sku = sku,
        productNumber = productNumber,
        productDescription = productDescription,
        quantity = quantity,
        unitPrice = unitPrice
    )
}
