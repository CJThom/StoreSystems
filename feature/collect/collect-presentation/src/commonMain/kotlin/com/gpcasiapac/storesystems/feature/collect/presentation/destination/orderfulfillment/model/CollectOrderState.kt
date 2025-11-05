package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.model

import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.common.presentation.fixture.PlaceholderValue
import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import kotlin.time.Clock
import kotlin.time.Instant

@Immutable
data class CollectOrderState(
    val invoiceNumber: InvoiceNumber,
    val orderNumber: String,
    val webOrderNumber: String?,
    val createdAt: Instant,
    val pickedAt: Instant,
    val signature: String? = null
) {

    companion object {

        fun placeholder(index: Int = 0): CollectOrderState {
            return CollectOrderState(
                invoiceNumber = InvoiceNumber("${PlaceholderValue.fixed(12)}$index"),
                orderNumber = PlaceholderValue.fixed(12),
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
