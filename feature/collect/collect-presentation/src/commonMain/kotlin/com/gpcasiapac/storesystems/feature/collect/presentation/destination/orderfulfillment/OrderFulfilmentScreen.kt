package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Person
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
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.presentation.component.CollectOrderDetails
import com.gpcasiapac.storesystems.feature.collect.presentation.components.ActionButton
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CollectionTypeSection
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CollectionTypeSectionDisplayParam
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CorrespondenceItemRow
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
    onOutcome: (outcome: OrderFulfilmentScreenContract.Effect.Outcome) -> Unit,
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

        Column(
            modifier = Modifier
                .padding(padding)
                .verticalScroll(rememberScrollState())
        ) {
            if (state.collectOrderWithCustomerWithLineItemsState != null) {
                SingleOrderContent(
                    orderState = state.collectOrderWithCustomerWithLineItemsState,
                    isProductListExpanded = state.visibleProductListItemCount > 2,
                    visibleLineItemListCount = state.visibleProductListItemCount,
                    onViewMoreClick = {
                        onEventSent(OrderFulfilmentScreenContract.Event.ToggleProductListExpansion)
                    })

            } else {
                Column(
                    modifier = Modifier.padding(Dimens.Space.medium),
                    verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
                ) {
                    state.collectOrderListItemStateList.forEach { collectOrderState ->
                        OutlinedCard(
                            modifier = Modifier.fillMaxWidth(),
                            onClick = {
                                onEventSent(OrderFulfilmentScreenContract.Event.OrderClicked(collectOrderState.invoiceNumber))
                            },
                            enabled = true
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
            }

            HorizontalDivider()

            CollectionTypeSection(
                onValueChange = { collectionType ->
                    onEventSent(OrderFulfilmentScreenContract.Event.CollectingChanged(collectionType))
                },
                modifier = Modifier.padding(horizontal = Dimens.Space.medium),
                title = stringResource(Res.string.who_is_collecting),
                value = state.collectingType,
                options = listOf(
                    CollectionTypeSectionDisplayParam(
                        enabled = true,
                        collectingType = CollectingType.STANDARD,
                        icon = Icons.Outlined.Person,
                        label = CollectingType.STANDARD.name,
                    ), CollectionTypeSectionDisplayParam(
                        enabled = true,
                        collectingType = CollectingType.ACCOUNT,
                        icon = Icons.Outlined.BusinessCenter,
                        label = CollectingType.ACCOUNT.name,
                    ), CollectionTypeSectionDisplayParam(
                        enabled = true,
                        collectingType = CollectingType.COURIER,
                        icon = Icons.Outlined.LocalShipping,
                        label = CollectingType.COURIER.name,
                    )
                ),
            )


            HorizontalDivider()

            SignatureSection(
                modifier = Modifier.padding(horizontal = Dimens.Space.medium),
                onSignClick = {
                    onEventSent(OrderFulfilmentScreenContract.Event.Sign)
                },
                onRetakeClick = {
                    onEventSent(OrderFulfilmentScreenContract.Event.ClearSignature)
                },
                signatureStrokes = state.signatureStrokes
            )

            CorrespondenceSection(
                modifier = Modifier.padding(horizontal = Dimens.Space.medium)
            ) {
                CorrespondenceItemRow(
                    "Email",
                    "Send email to customer",
                    isEnabled = state.emailChecked,
                    onEdit = {
                        onEventSent(OrderFulfilmentScreenContract.Event.EditEmail)
                    },
                    onCheckChange = {
                        onEventSent(OrderFulfilmentScreenContract.Event.ToggleEmail(!state.emailChecked))
                    })
                CorrespondenceItemRow(
                    "Print",
                    "Send invoice to printer",
                    isEnabled = state.printChecked,
                    onEdit = {
                        onEventSent(OrderFulfilmentScreenContract.Event.EditPrinter)
                    },
                    onCheckChange = {
                        onEventSent(OrderFulfilmentScreenContract.Event.TogglePrint(!state.printChecked))
                    })
            }


            // Confirm Button

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