package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedCard
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.window.core.layout.WindowSizeClass
import com.gpcasiapac.storesystems.feature.collect.presentation.component.CollectOrderDetails
import com.gpcasiapac.storesystems.feature.collect.presentation.component.OrderDetailsLarge
import com.gpcasiapac.storesystems.feature.collect.presentation.components.ActionButton
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CollectionTypeSection
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CorrespondenceSection
import com.gpcasiapac.storesystems.feature.collect.presentation.components.HeaderMedium
import com.gpcasiapac.storesystems.feature.collect.presentation.components.SignaturePreviewImage
import com.gpcasiapac.storesystems.foundation.component.MBoltAppBar
import com.gpcasiapac.storesystems.foundation.component.TopBarTitle
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import storesystems.feature.collect.collect_presentation.generated.resources.Res
import storesystems.feature.collect.collect_presentation.generated.resources.who_is_collecting


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun OrderFulfilmentScreen(
    state: OrderFulfilmentScreenContract.State,
    onEventSent: (event: OrderFulfilmentScreenContract.Event) -> Unit,
    effectFlow: Flow<OrderFulfilmentScreenContract.Effect>?,
    onOutcome: (outcome: OrderFulfilmentScreenContract.Effect.Outcome) -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }
    val scrollState = rememberScrollState()

    val adaptiveInfo = currentWindowAdaptiveInfo() // ⚠️ verify import
    val window = adaptiveInfo.windowSizeClass
    val isMediumPlus = window.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_MEDIUM_LOWER_BOUND)

    LaunchedEffect(effectFlow) {
        effectFlow?.collectLatest { effect ->
            when (effect) {
                is OrderFulfilmentScreenContract.Effect.ShowToast -> snackbarHostState.showSnackbar(
                    effect.message, duration = SnackbarDuration.Short
                )

                is OrderFulfilmentScreenContract.Effect.ShowError -> snackbarHostState.showSnackbar(
                    effect.error, duration = SnackbarDuration.Long
                )

                is OrderFulfilmentScreenContract.Effect.Outcome -> onOutcome(effect)
            }
        }
    }


    Scaffold(
        topBar = {
            MBoltAppBar(
                title = {
                    TopBarTitle("Order Confirmation")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onEventSent(OrderFulfilmentScreenContract.Event.Back)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                })
        }, snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { padding ->

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
        ) {
            if (state.collectOrderWithCustomerWithLineItemsState != null) {

                val useGrid = isMediumPlus
                val columns = Columns(if (useGrid) 2 else 1)

                // Policy: phones = 2 rows collapsed; larger = "all fits" (no button)
                val rowsCollapsed = if (isMediumPlus) Int.MAX_VALUE else 2

                val order = state.collectOrderWithCustomerWithLineItemsState

                if (order != null) {
                    val total = order.lineItemList.size
                    val calc = remember(total, columns, rowsCollapsed) {
                        computeVisibility(total, columns, rowsCollapsed)
                    }
                    val isExpanded = state.wantsExpandedProductList && calc.canExpand
                    val visible =
                        if (isExpanded) order.lineItemList else order.lineItemList.take(calc.collapsedCount)

                    OrderDetailsLarge(
                        orderState = order,
                        visibleList = visible,             // pre-sliced list
                        isProductListExpanded = isExpanded, // only for label
                        onViewMoreClick = if (calc.canExpand) {
                            { onEventSent(OrderFulfilmentScreenContract.Event.ToggleProductListExpansion) }
                        } else null,
                        useGrid = useGrid
                    )
                }
            } else {

                MultiOrderListSection(
                    state = state,
                    onEventSent = onEventSent
                )

            }

            HorizontalDivider()

            CollectionTypeSection(
                title = stringResource(Res.string.who_is_collecting),
                value = state.collectingType,
                optionList = state.collectionTypeOptionList,
                onValueChange = { collectionType ->
                    onEventSent(
                        OrderFulfilmentScreenContract.Event.CollectingChanged(
                            collectionType
                        )
                    )
                },
            )

            HorizontalDivider()

            SignaturePreviewImage(
                onSignClick = {
                    onEventSent(OrderFulfilmentScreenContract.Event.Sign)
                },
                onRetakeClick = {
                    onEventSent(OrderFulfilmentScreenContract.Event.ClearSignature)
                },
                image = state.collectOrderWithCustomerWithLineItemsState?.order?.signature
            )

            HorizontalDivider()

            CorrespondenceSection(
                correspondenceOptionList = state.correspondenceOptionList,
                onCheckedChange = { id ->
                    onEventSent(
                        OrderFulfilmentScreenContract.Event.ToggleCorrespondence(
                            id = id
                        )
                    )
                }
            )

            HorizontalDivider()

            ActionButton(
                modifier = Modifier.padding(Dimens.Space.medium),
                title = {
                    Text(
                        text = "Confirm",
                    )
                },
                onClick = {
                    onEventSent(OrderFulfilmentScreenContract.Event.Confirm)
                },
            )

        }
    }
}


@Composable
private fun MultiOrderListSection(
    state: OrderFulfilmentScreenContract.State,
    onEventSent: (event: OrderFulfilmentScreenContract.Event) -> Unit
) {
    HeaderMedium(
        text = "Order List",
        isLoading = state.isLoading,
        contentPadding = PaddingValues(
            start = Dimens.Space.medium,
            top = Dimens.Space.medium,
            end = Dimens.Space.medium
        )
    )

    state.collectOrderListItemStateList.forEach { collectOrderState ->
        OutlinedCard(
            // Add padding per item for grid spacing
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = Dimens.Space.medium),
            onClick = {
                onEventSent(
                    OrderFulfilmentScreenContract.Event.OrderClicked(
                        collectOrderState.invoiceNumber
                    )
                )
            }
        ) {
            CollectOrderDetails(
                customerName = collectOrderState.customerName,
                customerType = collectOrderState.customerType,
                invoiceNumber = collectOrderState.invoiceNumber,
                webOrderNumber = collectOrderState.webOrderNumber,
                pickedAt = collectOrderState.pickedAt,
                isLoading = state.isLoading,
                contendPadding = PaddingValues(Dimens.Space.medium),
            )
        }
    }
}

@JvmInline
value class Columns(val value: Int)
data class VisibilityCalc(val collapsedCount: Int, val canExpand: Boolean)

/**
 * @param total total items available
 * @param columns how many items per row in grid, or 1 for list
 * @param rowsCollapsed how many rows to show when collapsed
 */
private fun computeVisibility(total: Int, columns: Columns, rowsCollapsed: Int): VisibilityCalc {
    val maxCollapsed = if (rowsCollapsed == Int.MAX_VALUE) {
        // Avoid overflow, on large screens we show all.
        total
    } else {
        // This won't overflow since rowsCollapsed is small (e.g., 2)
        columns.value * rowsCollapsed
    }
    val collapsed = minOf(total, maxCollapsed)
    return VisibilityCalc(collapsedCount = collapsed, canExpand = total > collapsed)
}


@Preview(
    name = "Order Fulfilment",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5L,
    widthDp = 360,
    heightDp = 720
)
@Composable
private fun OrderFulfilmentScreenPreview(
    @PreviewParameter(OrderFulfilmentScreenStateProvider::class) state: OrderFulfilmentScreenContract.State
) {
    GPCTheme {
        OrderFulfilmentScreen(
            state = state,
            onEventSent = {},
            effectFlow = null,
            onOutcome = {})
    }
}
