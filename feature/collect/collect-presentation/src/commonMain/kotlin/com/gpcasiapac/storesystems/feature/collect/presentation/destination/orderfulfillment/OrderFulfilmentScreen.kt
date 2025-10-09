package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.collect.presentation.component.CollectOrderDetails
import com.gpcasiapac.storesystems.feature.collect.presentation.components.ActionButton
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CollectionTypeSection
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CorrespondenceSection
import com.gpcasiapac.storesystems.feature.collect.presentation.components.SignatureSection
import com.gpcasiapac.storesystems.feature.collect.presentation.components.SingleOrderContent
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderFulfilmentScreen(
    state: OrderFulfilmentScreenContract.State,
    onEventSent: (event: OrderFulfilmentScreenContract.Event) -> Unit,
    effectFlow: Flow<OrderFulfilmentScreenContract.Effect>?,
    onOutcome: (outcome: OrderFulfilmentScreenContract.Effect.Outcome) -> Unit
) {

    val snackbarHostState = remember { SnackbarHostState() }

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
            MBoltAppBar(title = {
                TopBarTitle("Order Confirmation")
            }, navigationIcon = {
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

        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 340.dp), // Each item needs at least 340dp
            modifier = Modifier.padding(padding),
            contentPadding = PaddingValues(bottom = Dimens.Space.medium), // Add padding here
            verticalArrangement = Arrangement.spacedBy(Dimens.Space.small)
        ) {
            if (state.collectOrderWithCustomerWithLineItemsState != null) {
                // SINGLE ORDER VIEW
                item(span = { GridItemSpan(maxLineSpan) }) {
                    SingleOrderContent(
                        orderState = state.collectOrderWithCustomerWithLineItemsState,
                        isProductListExpanded = state.visibleProductListItemCount > 2,
                        visibleLineItemListCount = state.visibleProductListItemCount,
                        onViewMoreClick = {
                            onEventSent(OrderFulfilmentScreenContract.Event.ToggleProductListExpansion)
                        }
                    )
                }

            } else {
                // MULTI-ORDER VIEW (GRID)
                // Use the lazy 'items' builder instead of forEach
                items(
                    items = state.collectOrderListItemStateList,
                    key = { it.invoiceNumber } // For performance
                ) { collectOrderState ->
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

            // The rest of the sections now span the full width of the grid
            item(span = { GridItemSpan(maxLineSpan) }) {
                HorizontalDivider()
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                CollectionTypeSection(
                    title = stringResource(Res.string.who_is_collecting),
                    value = state.collectingType,
                    optionList = state.collectionTypeOptionList,
                    onValueChange = { collectionType ->
                        onEventSent(OrderFulfilmentScreenContract.Event.CollectingChanged(collectionType))
                    },
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                HorizontalDivider()
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                SignatureSection(
                    onSignClick = {
                        onEventSent(OrderFulfilmentScreenContract.Event.Sign)
                    },
                    onRetakeClick = {
                        onEventSent(OrderFulfilmentScreenContract.Event.ClearSignature)
                    },
                    signatureStrokes = state.signatureStrokes
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
              HorizontalDivider()
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
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
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                ActionButton(
                    modifier = Modifier.padding(horizontal = Dimens.Space.medium),
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
        OrderFulfilmentScreen(state = state, onEventSent = {}, effectFlow = null, onOutcome = {})
    }
}