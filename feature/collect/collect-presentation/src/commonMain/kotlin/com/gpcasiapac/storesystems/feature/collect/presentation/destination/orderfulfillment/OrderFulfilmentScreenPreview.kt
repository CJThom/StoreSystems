package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Person
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.model.Representative
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CollectionTypeSectionDisplayState
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CorrespondenceItemDisplayParam
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.sampleCollectOrderListItemStateList
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.sampleCollectOrderWithCustomerWithLineItemsState
import org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider

class OrderFulfilmentScreenStateProvider : PreviewParameterProvider<OrderFulfilmentScreenContract.State> {
    override val values: Sequence<OrderFulfilmentScreenContract.State>
        get() {
            val orders = sampleCollectOrderListItemStateList().take(3)
            val order = sampleCollectOrderWithCustomerWithLineItemsState()

            val base = OrderFulfilmentScreenContract.State(
                // Single order context
                collectOrderWithCustomerWithLineItemsState = order,
                // Multi-order context
                collectOrderListItemStateList = emptyList(),
                // isMultiOrder = { false },
                // Flags
                isLoading = false,
                error = null,

                visibleProductListItemCount = 2,

                // Collecting
                collectingType = CollectingType.STANDARD,
                collectionTypeOptionList = listOf(
                    CollectionTypeSectionDisplayState(
                        enabled = true,
                        collectingType = CollectingType.STANDARD,
                        icon = Icons.Outlined.Person,
                        label = CollectingType.STANDARD.name,
                    ), CollectionTypeSectionDisplayState(
                        enabled = true,
                        collectingType = CollectingType.ACCOUNT,
                        icon = Icons.Outlined.BusinessCenter,
                        label = CollectingType.ACCOUNT.name,
                    ), CollectionTypeSectionDisplayState(
                        enabled = true,
                        collectingType = CollectingType.COURIER,
                        icon = Icons.Outlined.LocalShipping,
                        label = CollectingType.COURIER.name,
                    )
                ),

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
                correspondenceOptionList = listOf(
                    CorrespondenceItemDisplayParam(
                        id = "email",
                        type = "Email",
                        detail = "Send email to customer",
                        isEnabled = true
                    ),
                    CorrespondenceItemDisplayParam(
                        id = "print",
                        type = "Print",
                        detail = "Send invoice to printer",
                        isEnabled = true
                    )
                ),
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