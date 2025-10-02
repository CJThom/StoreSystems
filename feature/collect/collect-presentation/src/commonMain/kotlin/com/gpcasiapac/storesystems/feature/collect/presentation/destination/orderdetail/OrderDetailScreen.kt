package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.BackHand
import androidx.compose.material.icons.outlined.BusinessCenter
import androidx.compose.material.icons.outlined.LocalShipping
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Receipt
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
import com.gpcasiapac.storesystems.common.kotlin.util.StringUtils
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.presentation.components.ActionButton
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CollectionTypeSection
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CollectionTypeSectionDisplayParam
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CorrespondenceItemRow
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CorrespondenceSection
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CustomerDetails
import com.gpcasiapac.storesystems.feature.collect.presentation.components.MBoltSimpleAppBar
import com.gpcasiapac.storesystems.feature.collect.presentation.components.ProductListSection
import com.gpcasiapac.storesystems.feature.collect.presentation.components.SignatureSection
import com.gpcasiapac.storesystems.foundation.component.TopBarTitle
import com.gpcasiapac.storesystems.foundation.component.detailitem.DetailItemMedium
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.MBoltIcons
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
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
                is OrderDetailScreenContract.Effect.ShowToast ->
                    snackbarHostState.showSnackbar(
                        effect.message,
                        duration = SnackbarDuration.Short
                    )

                is OrderDetailScreenContract.Effect.ShowError ->
                    snackbarHostState.showSnackbar(effect.error, duration = SnackbarDuration.Long)

                is OrderDetailScreenContract.Effect.Outcome -> onOutcome(effect)
            }
        }
    }

    Scaffold(
        topBar = {
            MBoltSimpleAppBar(
                title = {
                    TopBarTitle("Order Confirmation")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onEventSent(OrderDetailScreenContract.Event.Back)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(Dimens.Space.large),
            contentPadding = padding
        ) {
            // Invoice Header
            item {
                Text(
                    text = "Invoice: ${state.collectOrder?.invoiceNumber.orEmpty()}",
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(
                        horizontal = Dimens.Space.medium,
                        vertical = Dimens.Space.large
                    )
                )
            }

            // Order Details Section
            item {
                Column(
                    modifier = Modifier.padding(horizontal = Dimens.Space.medium),
                    verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
                    ) {
                        DetailItemMedium(
                            modifier = Modifier.weight(1f),
                            label = "Sales Order Number",
                            value = state.collectOrder?.invoiceNumber.orEmpty(),
                            imageVector = Icons.Outlined.Receipt
                        )
                        DetailItemMedium(
                            modifier = Modifier.weight(1f),
                            label = "Web Order Number",
                            value = state.collectOrder?.webOrderNumber.orEmpty(),
                            imageVector = MBoltIcons.Globe
                        )
                    }
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
                    ) {
                        DetailItemMedium(
                            modifier = Modifier.weight(1f),
                            label = "Created",
                            value = "10341882849",
                            imageVector = MBoltIcons.CalendarAddOn
                        )
                        DetailItemMedium(
                            modifier = Modifier.weight(1f),
                            label = "Picked",
                            value = state.collectOrder?.pickedAt?.toString().orEmpty(),
                            imageVector = Icons.Outlined.BackHand
                        )
                    }
                }
            }
            item {
                HorizontalDivider()
            }
            // Customer Information
            item {
                CustomerDetails(
                    customerName = state.collectOrder?.customerName.orEmpty(),
                    customerNumber = "", // Not available in CollectOrderState
                    phoneNumber = "", // Not available in CollectOrderState
                    customerType = state.collectOrder?.customerType ?: CustomerType.B2B, // TODO: Use placeholder (greeking)
                    modifier = Modifier.padding(horizontal = Dimens.Space.medium)
                )
            }
            item {
                HorizontalDivider()
            }
            // Product List Section
            item {
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
            }
            item {
                HorizontalDivider()
            }

            // Collection Type Section
            item {
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
                        ),
                        CollectionTypeSectionDisplayParam(
                            enabled = true,
                            collectingType = CollectingType.ACCOUNT,
                            icon = Icons.Outlined.BusinessCenter,
                            label = CollectingType.ACCOUNT.name,
                        ),
                        CollectionTypeSectionDisplayParam(
                            enabled = true,
                            collectingType = CollectingType.COURIER,
                            icon = Icons.Outlined.LocalShipping,
                            label = CollectingType.COURIER.name,
                        )
                    ),
                )
            }
            item {
                HorizontalDivider()
            }

            // Signature Section
            item {
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
            }

            // Correspondence Section
            item {
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
                        }
                    )
                    CorrespondenceItemRow(
                        "Print",
                        "Send invoice to printer",
                        isEnabled = state.printChecked,
                        onEdit = {
                            onEventSent(OrderDetailScreenContract.Event.EditPrinter)
                        },
                        onCheckChange = {
                            onEventSent(OrderDetailScreenContract.Event.TogglePrint(!state.printChecked))
                        }
                    )
                }
            }

            // Confirm Button
            item {
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
