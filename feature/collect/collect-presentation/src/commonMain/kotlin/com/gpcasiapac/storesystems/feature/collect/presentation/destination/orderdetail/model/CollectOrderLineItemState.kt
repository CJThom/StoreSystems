package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail.model

import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderLineItem

@Immutable
data class CollectOrderLineItemState(
    val lineNumber: Int,
) {

    companion object {

        internal fun placeholder(index: Int = 0): CollectOrderLineItemState =
            CollectOrderLineItemState(
                lineNumber = index
            )

        internal fun placeholderList(count: Int = 6): List<CollectOrderLineItemState> =
            List(count) { index -> placeholder(index) }

    }

}


