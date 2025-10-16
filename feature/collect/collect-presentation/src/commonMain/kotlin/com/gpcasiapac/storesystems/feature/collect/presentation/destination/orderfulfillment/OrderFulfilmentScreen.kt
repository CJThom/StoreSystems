package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.PhoneAndroid
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.material3.placeholder
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.presentation.component.CollectOrderDetails
import com.gpcasiapac.storesystems.feature.collect.presentation.component.CollectionTypeSection
import com.gpcasiapac.storesystems.feature.collect.presentation.components.ActionButton
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CorrespondenceSection
import com.gpcasiapac.storesystems.feature.collect.presentation.components.HeaderMedium
import com.gpcasiapac.storesystems.feature.collect.presentation.components.SignaturePreviewImage
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.component.AccountCollectionContent
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.component.CourierCollectionContent
import com.gpcasiapac.storesystems.foundation.component.CheckboxCard
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
    val windowSizeClass =
        currentWindowAdaptiveInfo().windowSizeClass // TODO: Use BoxWithConstraints() to adapt to this screen size only
    val useColumns =
        !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    // Back confirmation dialog state; set via Effect.ShowSaveDiscardDialog
    val dialogSpec =
        remember { mutableStateOf<OrderFulfilmentScreenContract.Effect.ShowSaveDiscardDialog?>(null) }

    LaunchedEffect(effectFlow) {
        effectFlow?.collectLatest { effect ->
            when (effect) {
                is OrderFulfilmentScreenContract.Effect.ShowToast -> snackbarHostState.showSnackbar(
                    effect.message, duration = SnackbarDuration.Short
                )

                is OrderFulfilmentScreenContract.Effect.ShowError -> snackbarHostState.showSnackbar(
                    effect.error, duration = SnackbarDuration.Long
                )

                is OrderFulfilmentScreenContract.Effect.ShowSaveDiscardDialog -> {
                    dialogSpec.value = effect
                }

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
        // Single-column, scrollable layout for smaller screens
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(padding),
            verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
        ) {
            MultiOrderListSection(
                state = state,
                onEventSent = onEventSent
            )
            HorizontalDivider()
            ActionsContent(
                state = state,
                onEventSent = onEventSent
            )
            HorizontalDivider()
            ActionButton(
                modifier = Modifier.padding(Dimens.Space.medium),
                title = { Text(text = "Confirm") },
                onClick = { onEventSent(OrderFulfilmentScreenContract.Event.Confirm) },
            )
        }
    }

    // Global dialog after content so it overlays UI
    val spec = dialogSpec.value
    if (spec != null) {
        AlertDialog(
            onDismissRequest = {
                dialogSpec.value = null
                onEventSent(OrderFulfilmentScreenContract.Event.CancelBackDialog)
            },
            title = { Text(spec.title) },
            text = { Text(spec.message) },
            confirmButton = {
                TextButton(onClick = {
                    dialogSpec.value = null
                    onEventSent(OrderFulfilmentScreenContract.Event.ConfirmBackSave)
                }) {
                    Text(spec.saveLabel)
                }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small)) {
                    TextButton(onClick = {
                        dialogSpec.value = null
                        onEventSent(OrderFulfilmentScreenContract.Event.ConfirmBackDiscard)
                    }) {
                        Text(spec.discardLabel)
                    }
                    TextButton(onClick = {
                        dialogSpec.value = null
                        onEventSent(OrderFulfilmentScreenContract.Event.CancelBackDialog)
                    }) {
                        Text(spec.cancelLabel)
                    }
                }
            }
        )
    }
}


@Composable
private fun ActionsContent(
    state: OrderFulfilmentScreenContract.State,
    onEventSent: (event: OrderFulfilmentScreenContract.Event) -> Unit
) {
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
    ) { selectedType ->
        CollectionTypeContent(
            state = state,
            selectedType = selectedType,
            onEventSent = onEventSent
        )
    }

    HorizontalDivider()

    SignaturePreviewImage(
        onSignClick = {
            onEventSent(OrderFulfilmentScreenContract.Event.Sign)
        },
        onRetakeClick = {
            onEventSent(OrderFulfilmentScreenContract.Event.ClearSignature)
        },
        image = null
    )

    if (state.featureFlags.isCorrespondenceSectionVisible) {
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
    }
}

@Composable
private fun CollectionTypeContent(
    state: OrderFulfilmentScreenContract.State,
    selectedType: CollectingType,
    onEventSent: (event: OrderFulfilmentScreenContract.Event) -> Unit
) {
    when (selectedType) {
        CollectingType.ACCOUNT -> {
            if (state.featureFlags.isAccountCollectingFeatureEnabled) {
                AccountCollectionContent(
                    searchQuery = state.representativeSearchQuery,
                    onSearchQueryChange = { query ->
                        onEventSent(
                            OrderFulfilmentScreenContract.Event.RepresentativeSearchQueryChanged(
                                query
                            )
                        )
                    },
                    representatives = state.representativeList,
                    selectedRepresentativeIds = state.selectedRepresentativeIds,
                    onRepresentativeSelected = { id, isSelected ->
                        onEventSent(
                            OrderFulfilmentScreenContract.Event.RepresentativeSelected(
                                id,
                                isSelected
                            )
                        )
                    },
                    isLoading = state.isLoading
                )
            }
        }

        CollectingType.COURIER -> {
            CourierCollectionContent(
                courierName = state.courierName,
                onCourierNameChange = { name ->
                    onEventSent(OrderFulfilmentScreenContract.Event.CourierNameChanged(name))
                },
                isLoading = state.isLoading
            )
        }

        else -> {
            // No additional UI for STANDARD
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

    val items = state.collectOrderListItemStateList
    if (items.isEmpty()) {
        EmptyOrderPlaceholderCard()
    } else {
        items.forEach { collectOrderState ->
            CheckboxCard(
                // Add padding per item for grid spacing
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.Space.medium),
                isChecked = false,
                isCheckable = false,
                onCheckedChange = {

                },
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
}

// Placeholder card shown when there are no orders
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun EmptyOrderPlaceholderCard(
    modifier: Modifier = Modifier,
    isLoading: Boolean = false
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = Dimens.Space.medium)
            .clip(MaterialTheme.shapes.medium)
            .heightIn(min = 110.dp)
            .dashedBorder(color = MaterialTheme.colorScheme.outlineVariant),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(Dimens.Space.medium),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Outlined.PhoneAndroid, contentDescription = null,
                    modifier = Modifier.size(ButtonDefaults.iconSizeFor(ButtonDefaults.ExtraSmallContainerHeight))
                )
                Spacer(Modifier.width(ButtonDefaults.iconSpacingFor(ButtonDefaults.ExtraSmallContainerHeight)))
                Text(text = "Scan")
            }
            Text(
                text = "or",
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            OutlinedButton(
                onClick = { /* TODO: Lookup action */ },
                modifier = Modifier
                    .placeholder(isLoading)
                    .height(ButtonDefaults.ExtraSmallContainerHeight),
                contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.ExtraSmallContainerHeight)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Search,
                    contentDescription = "Lookup",
                    modifier = Modifier.size(ButtonDefaults.iconSizeFor(ButtonDefaults.ExtraSmallContainerHeight))
                )
                Spacer(Modifier.width(ButtonDefaults.iconSpacingFor(ButtonDefaults.ExtraSmallContainerHeight)))
                Text("Lookup")
            }
        }
    }
}

// Utility to draw a simple dashed rectangle border
private fun Modifier.dashedBorder(
    color: Color,
    strokeWidthPx: Float = 2f,
    intervals: FloatArray = floatArrayOf(12f, 8f)
): Modifier = this.drawBehind {
    val stroke = Stroke(
        width = strokeWidthPx,
        pathEffect = PathEffect.dashPathEffect(intervals, 0f)
    )
    drawRect(color = color, style = stroke)
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

