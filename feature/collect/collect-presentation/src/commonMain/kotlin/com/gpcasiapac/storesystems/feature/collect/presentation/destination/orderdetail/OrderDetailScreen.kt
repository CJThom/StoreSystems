package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
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
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CustomerDetails
import com.gpcasiapac.storesystems.feature.collect.presentation.components.ProductListSection
import com.gpcasiapac.storesystems.feature.collect.presentation.components.SignatureSection
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail.component.OrderDetails
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
fun OrderDetailScreen(
    state: OrderDetailScreenContract.State,
    onEventSent: (event: OrderDetailScreenContract.Event) -> Unit,
    effectFlow: Flow<OrderDetailScreenContract.Effect>?,
    onOutcome: (outcome: OrderDetailScreenContract.Effect.Outcome) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(effectFlow) {
        effectFlow?.collectLatest { effect ->
            when (effect) {
                is OrderDetailScreenContract.Effect.ShowToast -> snackbarHostState.showSnackbar(
                    effect.message, duration = SnackbarDuration.Short
                )

                is OrderDetailScreenContract.Effect.ShowError -> snackbarHostState.showSnackbar(
                    effect.error,
                    duration = SnackbarDuration.Long
                )

                is OrderDetailScreenContract.Effect.Outcome -> onOutcome(effect)
            }
        }
    }

    Scaffold(topBar = {
        MBoltAppBar(title = {
            TopBarTitle("Order Confirmation")
        }, navigationIcon = {
            IconButton(onClick = {
                onEventSent(OrderDetailScreenContract.Event.Back)
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
                OrderDetails(
                    invoiceNumber = state.collectOrderWithCustomerWithLineItemsState.order.invoiceNumber,
                    webOrderNumber = state.collectOrderWithCustomerWithLineItemsState.order.webOrderNumber,
                    createdAt = state.collectOrderWithCustomerWithLineItemsState.order.pickedAt, // TODO: get Order date
                    pickedAt = state.collectOrderWithCustomerWithLineItemsState.order.pickedAt,
                )

                HorizontalDivider()

                CustomerDetails(
                    customerName = state.collectOrderWithCustomerWithLineItemsState.customer.name,
                    customerNumber = state.collectOrderWithCustomerWithLineItemsState.customer.customerNumber,
                    phoneNumber = state.collectOrderWithCustomerWithLineItemsState.customer.mobileNumber,
                    customerType = state.collectOrderWithCustomerWithLineItemsState.customer.type,
                    modifier = Modifier
                )

                HorizontalDivider()

                ProductListSection(
                    modifier = Modifier.padding(horizontal = Dimens.Space.medium)
                ) {
//                    products.forEach { product ->
//                        ProductDetails(
//                            productName = product.name,
//                            productCode = product.code,
//                            price = product.price,
//                            quantity = product.quantity,
//                        )
//                    }
                }

            } else {
                state.collectOrderListItemStateList.forEach { collectOrderState ->

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

                HorizontalDivider()


                // Collection Type Section

                CollectionTypeSection(
                    onValueChange = { collectionType ->
                        onEventSent(OrderDetailScreenContract.Event.CollectingChanged(collectionType))
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


                // Signature Section

                SignatureSection(
                    modifier = Modifier.padding(horizontal = Dimens.Space.medium),
                    onSignClick = {
                        onEventSent(OrderDetailScreenContract.Event.Sign)
                    },
                    onRetakeClick = {
                        onEventSent(OrderDetailScreenContract.Event.ClearSignature)
                    },
                    signatureStrokes = state.signatureStrokes
                )


                // Correspondence Section

                CorrespondenceSection(
                    modifier = Modifier.padding(horizontal = Dimens.Space.medium)
                ) {
                    CorrespondenceItemRow(
                        "Email",
                        "Send email to customer",
                        isEnabled = state.emailChecked,
                        onEdit = {
                            onEventSent(OrderDetailScreenContract.Event.EditEmail)
                        },
                        onCheckChange = {
                            onEventSent(OrderDetailScreenContract.Event.ToggleEmail(!state.emailChecked))
                        })
                    CorrespondenceItemRow(
                        "Print",
                        "Send invoice to printer",
                        isEnabled = state.printChecked,
                        onEdit = {
                            onEventSent(OrderDetailScreenContract.Event.EditPrinter)
                        },
                        onCheckChange = {
                            onEventSent(OrderDetailScreenContract.Event.TogglePrint(!state.printChecked))
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
                        onEventSent(OrderDetailScreenContract.Event.Confirm)
                    },
                )
            }
        }
    }
}


@Preview(
    name = "Order detail",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5L,
    widthDp = 360,
    heightDp = 720
)
@Composable
private fun OrderDetailScreenPreview(
    @PreviewParameter(OrderDetailScreenStateProvider::class) state: OrderDetailScreenContract.State
) {
    GPCTheme {
        OrderDetailScreen(state = state, onEventSent = {}, effectFlow = null, onOutcome = {})
    }
}
