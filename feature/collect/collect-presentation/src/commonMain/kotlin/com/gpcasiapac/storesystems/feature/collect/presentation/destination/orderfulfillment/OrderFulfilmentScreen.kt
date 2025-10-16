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
import androidx.compose.foundation.shape.CircleShape
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
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.rememberSearchBarState
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
import com.gpcasiapac.storesystems.feature.collect.presentation.components.MBoltSearchBar
import com.gpcasiapac.storesystems.feature.collect.presentation.components.MBoltSearchExpandedOverlay
import com.gpcasiapac.storesystems.feature.collect.presentation.components.SignaturePreviewImage
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.component.AccountCollectionContent
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.component.CourierCollectionContent
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.OrderListScreenContract
import com.gpcasiapac.storesystems.foundation.component.CheckboxCard
import com.gpcasiapac.storesystems.foundation.component.MBoltAppBar
import com.gpcasiapac.storesystems.foundation.component.TopBarTitle
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import com.gpcasiapac.storesystems.feature.collect.presentation.search.SearchContract
import org.jetbrains.compose.resources.stringResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import storesystems.feature.collect.collect_presentation.generated.resources.Res
import storesystems.feature.collect.collect_presentation.generated.resources.who_is_collecting

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
    onEventSent: (event: OrderFulfilmentScreenContract.Event) -> Unit,
    onLookupClick: () -> Unit
) {
//    HeaderMedium(
//        text = "Order List",
//        isLoading = state.isLoading,
//        contentPadding = PaddingValues(
//            start = Dimens.Space.medium,
//            top = Dimens.Space.medium,
//            end = Dimens.Space.medium
//        )
//    )

    val items = state.collectOrderListItemStateList
    if (items.isEmpty()) {
        EmptyOrderPlaceholderCard(onLookupClick)
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
    onLookupClick: () -> Unit,
    modifier: Modifier = Modifier,
    isLoading: Boolean = false,
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
                onClick = onLookupClick,
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
            searchState = SearchContract.State(
                searchText = "",
                searchResults = emptyList(),
                orderSearchSuggestionList = emptyList(),
                isSearchActive = false
            ),
            onEventSent = {},
            onSearchEventSent = {},
            effectFlow = null,
            onOutcome = {})
    }
}



@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun OrderFulfilmentScreen(
    state: OrderFulfilmentScreenContract.State,
    searchState: SearchContract.State?,
    onEventSent: (event: OrderFulfilmentScreenContract.Event) -> Unit,
    onSearchEventSent: ((event: SearchContract.Event) -> Unit)?,
    effectFlow: Flow<OrderFulfilmentScreenContract.Effect>?,
    onOutcome: (outcome: OrderFulfilmentScreenContract.Effect.Outcome) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    val useColumns = !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)

    val dialogSpec = remember { mutableStateOf<OrderFulfilmentScreenContract.Effect.ShowSaveDiscardDialog?>(null) }

    // Search bar state management for the expanded overlay
    val searchBarState = rememberSearchBarState(
        initialValue = SearchBarValue.Collapsed
    )

//    val searchBarState = rememberSearchBarState(
//        initialValue = if (searchState.isSearchActive) SearchBarValue.Expanded else SearchBarValue.Collapsed
//    )

    // Keep search bar animation in sync with SearchViewModel
    if (searchState != null) {
        LaunchedEffect(searchState.isSearchActive) {
            if (searchState.isSearchActive) {
                searchBarState.animateToExpanded()
            } else {
                searchBarState.animateToCollapsed()
            }
        }
    }

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
                title = { TopBarTitle("Order Confirmation") },
                navigationIcon = {
                    IconButton(onClick = { onEventSent(OrderFulfilmentScreenContract.Event.Back) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
//        Box(
//            modifier = Modifier
//                .fillMaxSize()
//                .padding(padding)
//        ) {
            // Main content
            Column(
                modifier = Modifier
                    .padding(padding)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
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
                if(searchState != null && onSearchEventSent != null) {
                    MBoltSearchBar(
                        query = searchState.searchText,
                        onQueryChange = { query ->
                            onSearchEventSent(SearchContract.Event.SearchTextChanged(query))
                        },
                        searchBarState = searchBarState,
                        onSearch = { query ->
                            onSearchEventSent(SearchContract.Event.SearchTextChanged(query))
                        },
                        onExpandedChange = { isExpanded ->
                            onSearchEventSent(SearchContract.Event.SearchOnExpandedChange(isExpanded))
                        },
                        onBackPressed = {
                            onSearchEventSent(SearchContract.Event.SearchBarBackPressed)
                        },
                        onResultClick = { result ->
                            onSearchEventSent(SearchContract.Event.SearchResultClicked(result))
                        },
                        onClearClick = {
                            onSearchEventSent(SearchContract.Event.ClearSearch)
                        },
                        searchResults = searchState.orderSearchSuggestionList.map { it.text },
                        searchOrderItems = searchState.searchResults,
                        isMultiSelectionEnabled = false,
                        selectedOrderIdList = emptySet(),
                        isSelectAllChecked = false,
                        isRefreshing = false,
                        onOpenOrder = { id ->
                            //  onEventSent(OrderListScreenContract.Event.OpenOrder(id))
                        },
                        onCheckedChange = { orderId, checked ->
//                        onEventSent(
//                            OrderListScreenContract.Event.OrderChecked(
//                                orderId = orderId,
//                                checked = checked
//                            )
//                        )
                        },
                        onSelectAllToggle = { checked ->
                            // onEventSent(OrderListScreenContract.Event.SelectAll(checked))
                        },
                        onCancelSelection = {
                            //  onEventSent(OrderListScreenContract.Event.CancelSelection)
                        },
                        onEnterSelectionMode = {
//                        onEventSent(
//                            OrderListScreenContract.Event.ToggleSelectionMode(
//                                enabled = true
//                            )
//                        )
                        },
                        onSelectClick = {
                            //  onEventSent(OrderListScreenContract.Event.ConfirmSelection)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholderText = "Search by Order #, Name, Phone",
                        collapsedShape = CircleShape,
                        collapsedColors = SearchBarDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            inputFieldColors = SearchBarDefaults.inputFieldColors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                            )
                        )
                    )
                }
                MultiOrderListSection(
                    state = state,
                    onEventSent = onEventSent,
                    onLookupClick = {
                        if(onSearchEventSent != null) {
                            onSearchEventSent(SearchContract.Event.SearchOnExpandedChange(true))
                        }
                    }
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

//            // Search overlay (expanded only; no persistent collapsed bar)
//            MBoltSearchExpandedOverlay(
//                query = searchState.searchText,
//                onQueryChange = { q -> onSearchEventSent(SearchContract.Event.SearchTextChanged(q)) },
//                searchBarState = searchBarState,
//                onSearch = { q -> onSearchEventSent(SearchContract.Event.SearchTextChanged(q)) },
//                onExpandedChange = { expanded -> onSearchEventSent(SearchContract.Event.SearchOnExpandedChange(expanded)) },
//                onBackPressed = { onSearchEventSent(SearchContract.Event.SearchBarBackPressed) },
//                onClearClick = { onSearchEventSent(SearchContract.Event.ClearSearch) },
//                searchOrderItems = searchState.searchResults,
//                isMultiSelectionEnabled = false,
//                selectedOrderIdList = emptySet(),
//                isSelectAllChecked = false,
//                isRefreshing = state.isLoading,
//                onOpenOrder = { id -> onEventSent(OrderFulfilmentScreenContract.Event.OrderClicked(id)) },
//                onCheckedChange = { _, _ -> },
//                onSelectAllToggle = {},
//                onCancelSelection = {},
//                onEnterSelectionMode = {},
//                onSelectClick = {},
//                modifier = Modifier.fillMaxWidth(),
//                placeholderText = "Search by Order #, Name, Phone"
//            )
      //  }
    }

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
                }) { Text(spec.saveLabel) }
            },
            dismissButton = {
                Row(horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small)) {
                    TextButton(onClick = {
                        dialogSpec.value = null
                        onEventSent(OrderFulfilmentScreenContract.Event.ConfirmBackDiscard)
                    }) { Text(spec.discardLabel) }
                    TextButton(onClick = {
                        dialogSpec.value = null
                        onEventSent(OrderFulfilmentScreenContract.Event.CancelBackDialog)
                    }) { Text(spec.cancelLabel) }
                }
            }
        )
    }
}
