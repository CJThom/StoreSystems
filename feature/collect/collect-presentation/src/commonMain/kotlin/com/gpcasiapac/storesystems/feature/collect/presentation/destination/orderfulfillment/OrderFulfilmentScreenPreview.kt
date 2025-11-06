package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Person
import androidx.compose.ui.tooling.preview.PreviewParameterProvider
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.model.Representative
import com.gpcasiapac.storesystems.feature.collect.presentation.component.CollectionTypeSectionDisplayState
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CorrespondenceItemDisplayParam
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.sampleCollectOrderListItemStateList

class OrderFulfilmentScreenStateProvider :
    PreviewParameterProvider<OrderFulfilmentScreenContract.State> {
    override val values: Sequence<OrderFulfilmentScreenContract.State>
        get() {
            val orders = sampleCollectOrderListItemStateList().take(3)

            val representativeList = listOf(
                Representative("rep-1", "John Doe", "#9288180049912"),
                Representative("rep-2", "Custa Ma", "#9288180049912"),
                Representative("rep-3", "Alice Smith", "#9288180049912"),
            )

            val base = OrderFulfilmentScreenContract.State(
                // Multi-order context
                collectOrderListItemStateList = emptyList(),
                // isMultiOrder = { false },
                // Flags
                isLoading = false,
                isProcessing = false,
                error = null,

                featureFlags = OrderFulfilmentScreenContract.State.FeatureFlags(
                    isAccountRepresentativeSelectionFeatureEnabled = true,
                    isCorrespondenceSectionVisible = true
                ),

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
                        icon = Icons.Outlined.Business,
                        label = CollectingType.ACCOUNT.name,
                    ), CollectionTypeSectionDisplayState(
                        enabled = true,
                        collectingType = CollectingType.COURIER,
                        icon = Icons.Outlined.LocalShipping,
                        label = CollectingType.COURIER.name,
                    )
                ),

                // Account flow

                representativeSearchQuery = "",
                representativeList = representativeList,
                selectedRepresentativeIds = emptySet(),

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

            val singleB2C_Sighted = singleB2C.copy(
                isSighted = true
            )

            val singleB2C_Verified = singleB2C.copy(
                idVerified = true
            )

            val multiB2C = base.copy(
                collectOrderListItemStateList = orders,
                collectingType = CollectingType.STANDARD,
            )

            val accountFlow = base.copy(
                collectOrderListItemStateList = orders.take(2),
                collectingType = CollectingType.ACCOUNT,
                representativeSearchQuery = "Jo",
                representativeList = representativeList.filter {
                    it.name.contains(
                        "Jo",
                        ignoreCase = true
                    )
                },
                selectedRepresentativeIds = setOf("rep-1"),
            )

            val accountFlowFeatureOff = accountFlow.copy(
                featureFlags = accountFlow.featureFlags.copy(isAccountRepresentativeSelectionFeatureEnabled = false)
            )

            val courierFlow = base.copy(
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

            return sequenceOf(
                singleB2C,
                singleB2C_Sighted,
                singleB2C_Verified,
                multiB2C,
                accountFlow,
                accountFlowFeatureOff,
                courierFlow,
                loading,
                error
            )
        }
}