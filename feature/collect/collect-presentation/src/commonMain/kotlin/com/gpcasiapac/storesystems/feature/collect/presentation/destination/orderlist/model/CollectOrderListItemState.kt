package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model

import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.common.presentation.fixture.PlaceholderValue
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import kotlin.time.Clock
import kotlin.time.Instant

@Immutable
data class CollectOrderListItemState(
    val invoiceNumber: String,
    val webOrderNumber: String?,
    val customerType: CustomerType,
    val customerName: String,
    val pickedAt: Instant,
){

    companion object{

        internal fun placeholder(index: Int = 0): CollectOrderListItemState {
            return CollectOrderListItemState(
                invoiceNumber = "${PlaceholderValue.fixed(12)}$index",
                webOrderNumber = PlaceholderValue.fixed(12),
                customerType = if (index % 2 == 0) CustomerType.B2C else CustomerType.B2B,
                customerName = PlaceholderValue.variable(
                    minLength = 10,
                    maxLength = 25
                ),
                pickedAt = Clock.System.now()
            )
        }

        internal fun placeholderList(count: Int = 6): List<CollectOrderListItemState> {
            return List(count) { index -> placeholder(index) }
        }

    }
}
