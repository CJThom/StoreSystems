package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.model.Representative
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.sampleCollectOrderListItemStateList
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.sampleCollectOrderWithCustomerWithLineItemsState
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

class OrderDetailScreenStateProvider : PreviewParameterProvider<OrderDetailScreenContract.State> {
    override val values: Sequence<OrderDetailScreenContract.State>
        get() {
            val orders = sampleCollectOrderListItemStateList()
            val order = sampleCollectOrderWithCustomerWithLineItemsState()

            val base = OrderDetailScreenContract.State(
                // Single order context
                collectOrderWithCustomerWithLineItemsState = order,
                // Multi-order context
                collectOrderListItemStateList = emptyList(),
                isMultiOrder = { false },
                // Flags
                isLoading = false,
                error = null,

                // Collecting
                collectingType = CollectingType.STANDARD,

                // Account flow
                representativeSearchText = "",
                recentRepresentativeList = listOf(
                    Representative("rep-1", "John Doe", "#9288180049912"),
                    Representative("rep-2", "Custa Ma", "#9288180049912"),
                    Representative("rep-3", "Alice Smith", "#9288180049912"),
                ),
                selectedRepresentativeIdList = emptySet(),

                // Courier
                courierName = "",

                // Signature
                signatureStrokes = emptyList(),

                // Correspondence
                emailChecked = true,
                printChecked = true,
            )

            val singleB2C = base.copy(
                collectOrderListItemStateList = orders.take(1)
            )

            val multiB2C = base.copy(
                collectOrderWithCustomerWithLineItemsState = null,
                collectOrderListItemStateList = orders,
                collectingType = CollectingType.STANDARD,
            )

            val accountFlow = base.copy(
                collectOrderWithCustomerWithLineItemsState = null,
                collectOrderListItemStateList = orders.take(2),
                collectingType = CollectingType.ACCOUNT,
                representativeSearchText = "Jo",
                selectedRepresentativeIdList = setOf("rep-1"),
            )

            val courierFlow = base.copy(
                collectOrderWithCustomerWithLineItemsState = null,
                collectOrderListItemStateList = orders,
                collectingType = CollectingType.COURIER,
                courierName = "DHL Express",
            )

            val loading = base.copy(
                isLoading = true,
                collectOrderListItemStateList = emptyList(),
            )

            val error = base.copy(
                error = "Failed to load order. Please try again.",
            )

            return sequenceOf(singleB2C, multiB2C, accountFlow, courierFlow, loading, error)
        }
}
