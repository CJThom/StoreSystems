package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail.model

import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.common.presentation.fixture.PlaceholderValue
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import kotlin.time.Clock
import kotlin.time.Instant

@Immutable
data class CollectOrderState(
    val id: String,
    val invoiceNumber: String,
    val salesOrderNumber: String,
    val webOrderNumber: String?,
    val createdAt: Instant,
    val pickedAt: Instant
) {

    companion object {

        fun placeholder(index: Int = 0): CollectOrderState {
            return CollectOrderState(
                id = "PLACEHOLDER_$index",
                invoiceNumber = PlaceholderValue.fixed(12),
                salesOrderNumber = PlaceholderValue.fixed(12),
                webOrderNumber = PlaceholderValue.fixed(12),
                createdAt = Clock.System.now(),
                pickedAt = Clock.System.now()
            )
        }

        fun placeholderList(count: Int = 6): List<CollectOrderState> {
            return  List(count) { index -> placeholder(index) }
        }

    }

}
