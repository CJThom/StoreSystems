package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.outlined.DoneAll
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
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.currentWindowAdaptiveInfo
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.window.core.layout.WindowSizeClass
import coil3.size.Dimension
import com.gpcasiapac.storesystems.common.feedback.haptic.HapticPerformer
import com.gpcasiapac.storesystems.common.feedback.sound.SoundPlayer
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.material3.placeholder
import com.gpcasiapac.storesystems.common.presentation.compose.theme.borderStroke
import com.gpcasiapac.storesystems.common.presentation.compose.theme.dashedBorder
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.component.CollectOrderFulfilmentItem
import com.gpcasiapac.storesystems.feature.collect.presentation.component.CollectionTypeSection
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.search.MBoltSearchBar
import com.gpcasiapac.storesystems.feature.collect.presentation.components.ActionButton
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CorrespondenceSection
import com.gpcasiapac.storesystems.feature.collect.presentation.components.HeaderMedium
import com.gpcasiapac.storesystems.feature.collect.presentation.components.SignaturePreviewImage
import com.gpcasiapac.storesystems.feature.collect.presentation.components.IdVerificationSection
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.component.AccountCollectionContent
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.component.CourierCollectionContent
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.search.SearchContract
import com.gpcasiapac.storesystems.feature.collect.presentation.selection.SelectionContract
import com.gpcasiapac.storesystems.foundation.component.CheckboxCard
import com.gpcasiapac.storesystems.foundation.component.MBoltAppBar
import com.gpcasiapac.storesystems.foundation.component.TopBarTitle
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.resources.stringResource
import storesystems.feature.collect.collect_presentation.generated.resources.Res
import storesystems.feature.collect.collect_presentation.generated.resources.who_is_collecting


@OptIn(
    ExperimentalMaterial3Api::class,
    ExperimentalMaterial3AdaptiveApi::class,
    ExperimentalMaterial3ExpressiveApi::class
)
@Composable
fun OrderFulfilmentScreen(
    state: OrderFulfilmentScreenContract.State,
    searchState: SearchContract.State?,
    onEventSent: (event: OrderFulfilmentScreenContract.Event) -> Unit,
    onSearchEventSent: ((event: SearchContract.Event) -> Unit)?,
    effectFlow: Flow<OrderFulfilmentScreenContract.Effect>?,
    onOutcome: (outcome: OrderFulfilmentScreenContract.Effect.Outcome) -> Unit,
    soundPlayer: SoundPlayer? = null,
    hapticPerformer: HapticPerformer? = null,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val windowSizeClass = currentWindowAdaptiveInfo().windowSizeClass
    !windowSizeClass.isWidthAtLeastBreakpoint(WindowSizeClass.WIDTH_DP_EXPANDED_LOWER_BOUND)


    // Parent-driven confirm dialog for search selection (2-button)
    val selectionConfirmDialogSpec = remember {
        mutableStateOf<OrderFulfilmentScreenContract.Effect.ShowConfirmSelectionDialog?>(null)
    }

    // Search bar state management for the expanded overlay
    val searchBarState = rememberSearchBarState(
        initialValue = SearchBarValue.Collapsed
    )

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

                is OrderFulfilmentScreenContract.Effect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        actionLabel = effect.actionLabel,
                        duration = effect.duration
                    )
                }

                is OrderFulfilmentScreenContract.Effect.PlaySound -> {
                    soundPlayer?.play(effect.soundEffect)
                }

                is OrderFulfilmentScreenContract.Effect.PlayHaptic -> {
                    hapticPerformer?.perform(effect.type)
                }


                is OrderFulfilmentScreenContract.Effect.ShowConfirmSelectionDialog -> {
                    selectionConfirmDialogSpec.value = effect
                }

                is OrderFulfilmentScreenContract.Effect.CollapseSearchBar -> {
                    onSearchEventSent?.invoke(SearchContract.Event.SearchOnExpandedChange(false))
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
        LazyVerticalGrid(
            columns = androidx.compose.foundation.lazy.grid.GridCells.Adaptive(minSize = 320.dp),
            modifier = Modifier.fillMaxSize(),
            contentPadding = padding,
            horizontalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
        ) {

            item { Box(Modifier.size(Dimens.Space.medium)) }

            // Header
            item(span = { GridItemSpan(maxLineSpan) }) {
                HeaderMedium(
                    text = "Order List",
                    isLoading = state.isLoading,
                    contentPadding = PaddingValues(
                        horizontal = Dimens.Space.medium,
                        vertical = Dimens.Space.small
                    )
                )
            }

            // Search bar (full span)
            // Required hacky hiding to simulate 'Lookup' button expansion
            if ((searchState != null && onSearchEventSent != null)) {
                item(span = { GridItemSpan(maxLineSpan) }) {
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
                            onSearchEventSent(
                                SearchContract.Event.SearchOnExpandedChange(
                                    isExpanded
                                )
                            )
                        },
                        onBackPressed = {
                            onSearchEventSent(SearchContract.Event.SearchBarBackPressed)
                        },
                        onResultClick = { result ->
                            onSearchEventSent(
                                SearchContract.Event.SearchResultClicked(result)
                            )
                        },
                        onClearClick = {
                            onSearchEventSent(SearchContract.Event.ClearSearch)
                        },
                        recentSearches = emptyList(),
                        suggestions = searchState.searchSuggestions,
                        onSuggestionClicked = { s ->
                            onSearchEventSent(SearchContract.Event.SearchSuggestionClicked(s))
                        },
                        selectedChips = searchState.selectedChips,
                        typedSuffix = searchState.typedSuffix,
                        onTypedSuffixChange = { text ->
                            onSearchEventSent(SearchContract.Event.TypedSuffixChanged(text))
                        },
                        onRemoveChip = { s ->
                            onSearchEventSent(SearchContract.Event.RemoveChip(s))
                        },
                        searchOrderItems = searchState.searchOrderItems,
                        isMultiSelectionEnabled = searchState.selection.isEnabled,
                        selectedOrderIdList = searchState.selection.selected,
                        isSelectAllChecked = searchState.selection.isAllSelected,
                        isRefreshing = state.isLoading,
                        onOpenInvoice = { id ->
                            onEventSent(OrderFulfilmentScreenContract.Event.OrderClicked(id))
                        },
                        onCheckedChange = { orderId, checked ->
                            onSearchEventSent(
                                SearchContract.Event.Selection(
                                    SelectionContract.Event.SetItemChecked(orderId, checked)
                                )
                            )
                        },
                        onSelectAllToggle = { checked ->
                            onSearchEventSent(
                                SearchContract.Event.Selection(
                                    SelectionContract.Event.SelectAll(checked)
                                )
                            )
                        },
                        onCancelSelection = {
                            onSearchEventSent(
                                SearchContract.Event.Selection(
                                    SelectionContract.Event.Cancel
                                )
                            )
                        },
                        onEnterSelectionMode = {
                            onSearchEventSent(
                                SearchContract.Event.Selection(
                                    SelectionContract.Event.ToggleMode(true)
                                )
                            )
                        },
                        onSelectClick = {
                            onEventSent(OrderFulfilmentScreenContract.Event.ConfirmSearchSelection)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .then(
                                if (state.collectOrderListItemStateList.isEmpty()) Modifier.size(0.dp) else Modifier
                            ),
                        placeholderText = "Search by Order #, Name, Phone",
                        collapsedContentPadding = PaddingValues(
                            horizontal = Dimens.Space.medium,
                            vertical = Dimens.Space.small
                        ),
                        collapsedShape = CircleShape,
                        collapsedColors = SearchBarDefaults.colors(
                            containerColor = MaterialTheme.colorScheme.surfaceContainer,
                            inputFieldColors = SearchBarDefaults.inputFieldColors(
                                focusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                                unfocusedContainerColor = MaterialTheme.colorScheme.surfaceContainer,
                                disabledContainerColor = MaterialTheme.colorScheme.surfaceContainer
                            )
                        ),
                        collapsedBorder = MaterialTheme.borderStroke()
                    )
                }
            }

            if (state.collectOrderListItemStateList.isEmpty()) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    EmptyOrderPlaceholderCard(
                        onLookupClick = {
                            if (onSearchEventSent != null) {
                                onSearchEventSent(SearchContract.Event.SearchOnExpandedChange(true))
                            }
                        }
                    )
                }
            } else {
                // Orders grid
                items(
                    items = state.collectOrderListItemStateList,
                    key = { it.invoiceNumber.value },
                ) { collectOrderState ->
                    CheckboxCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(
                                horizontal = Dimens.Space.medium,
                                vertical = Dimens.Space.small
                            ).animateItem(),
                        isChecked = false,
                        isCheckable = false,
                        onCheckedChange = {},
                        onClick = {
                            onEventSent(
                                OrderFulfilmentScreenContract.Event.OrderClicked(
                                    collectOrderState.invoiceNumber
                                )
                            )
                        }
                    ) {
                        CollectOrderFulfilmentItem(
                            customerName = collectOrderState.customerName,
                            customerType = collectOrderState.customerType,
                            invoiceNumber = collectOrderState.invoiceNumber,
                            webOrderNumber = collectOrderState.webOrderNumber,
                            pickedAt = collectOrderState.pickedAt,
                            isLoading = state.isLoading,
                            onDelete = {
                                onEventSent(
                                    OrderFulfilmentScreenContract.Event.DeselectOrder(
                                        collectOrderState.invoiceNumber
                                    )
                                )
                            }
                        )
                    }
                }
            }

            if (state.collectOrderListItemStateList.isNotEmpty()) {
                item { Box(Modifier.size(Dimens.Space.medium)) }
            }

            // Divider and actions (full span)
            item(span = { GridItemSpan(maxLineSpan) }) {
                HorizontalDivider(
                    modifier = Modifier.padding(
                        vertical = Dimens.Space.medium
                    )
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                CollectorSection(
                    state = state,
                    onEventSent = onEventSent
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                HorizontalDivider(
                    modifier = Modifier.padding(
                        vertical = Dimens.Space.medium
                    )
                )
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                SignaturePreviewImage(
                    onSignClick = {
                        onEventSent(OrderFulfilmentScreenContract.Event.Sign)
                    },
                    onRetakeClick = {
                        onEventSent(OrderFulfilmentScreenContract.Event.ClearSignature)
                    },
                    image = state.signatureBase64
                )
            }

            if (state.featureFlags.isCorrespondenceSectionVisible) {
                item(span = { GridItemSpan(maxLineSpan) }) {
                    HorizontalDivider(
                        modifier = Modifier.padding(
                            vertical = Dimens.Space.medium
                        )
                    )
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
            }

            item(span = { GridItemSpan(maxLineSpan) }) {
                HorizontalDivider(
                    modifier = Modifier.padding(
                        vertical = Dimens.Space.medium
                    )
                )
            }
            item(span = { GridItemSpan(maxLineSpan) }) {
                ActionButton(
                    modifier = Modifier.padding(Dimens.Space.medium),
                    title = {
                        if (state.isProcessing) {
                            androidx.compose.material3.CircularProgressIndicator(
                                modifier = Modifier.size(ButtonDefaults.IconSize),
                                color = MaterialTheme.colorScheme.onTertiary,
                                strokeWidth = 2.dp
                            )
                            Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(ButtonDefaults.MediumContainerHeight)))
                            Text(
                                text = "PROCESSING...",
                                style = MaterialTheme.typography.labelLarge
                            )
                        } else {
                            Icon(imageVector = Icons.Outlined.DoneAll, contentDescription = "Done")
                            Spacer(Modifier.size(ButtonDefaults.iconSpacingFor(ButtonDefaults.MediumContainerHeight)))
                            Text(text = "CONFIRM", style = MaterialTheme.typography.labelLarge)
                        }
                    },
                    onClick = {
                        if (!state.isProcessing && !state.isLoading) {
                            onEventSent(OrderFulfilmentScreenContract.Event.Confirm)
                        }
                    },
                )
            }
        }


        // Search selection confirmation dialog (parent-driven, 2-button)
        val selectSpec = selectionConfirmDialogSpec.value
        if (selectSpec != null) {
            AlertDialog(
                onDismissRequest = {
                    selectionConfirmDialogSpec.value = null
                    onEventSent(OrderFulfilmentScreenContract.Event.DismissConfirmSearchSelectionDialog)
                },
                title = { Text(selectSpec.title) },
                confirmButton = {
                    TextButton(onClick = {
                        selectionConfirmDialogSpec.value = null
                        if (onSearchEventSent != null) {
                            onSearchEventSent(SearchContract.Event.Selection(SelectionContract.Event.ConfirmProceed))
                        }
                        onEventSent(OrderFulfilmentScreenContract.Event.ConfirmSearchSelectionProceed)
                    }) { Text(selectSpec.confirmLabel) }
                },
                dismissButton = {
                    TextButton(onClick = {
                        selectionConfirmDialogSpec.value = null
                        onEventSent(OrderFulfilmentScreenContract.Event.DismissConfirmSearchSelectionDialog)
                    }) { Text(selectSpec.cancelLabel) }
                }
            )
        }
    }
    //  }

    // Customer name dialog
    if (state.isCustomerNameDialogVisible) {
        AlertDialog(
            onDismissRequest = {
                onEventSent(OrderFulfilmentScreenContract.Event.DismissCustomerNameDialog)
            },
            title = { Text("Enter Customer Name") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(Dimens.Space.small)) {
                    Text("This name will be saved with the signature.")
                    OutlinedTextField(
                        value = state.customerNameInput,
                        onValueChange = {
                            onEventSent(
                                OrderFulfilmentScreenContract.Event.CustomerNameChanged(
                                    it
                                )
                            )
                        },
                        singleLine = true,
                        label = { Text("Customer Name") },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(
                    enabled = state.customerNameInput.trim().isNotEmpty(),
                    onClick = { onEventSent(OrderFulfilmentScreenContract.Event.ConfirmCustomerName) }
                ) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = { onEventSent(OrderFulfilmentScreenContract.Event.DismissCustomerNameDialog) }) {
                    Text(
                        "Cancel"
                    )
                }
            }
        )
    }
}

@Composable
private fun CollectorSection(
    state: OrderFulfilmentScreenContract.State,
    onEventSent: (event: OrderFulfilmentScreenContract.Event) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.medium)
) {

    Column(
        modifier = modifier
            .padding(
                top = contentPadding.calculateTopPadding(),
                bottom = contentPadding.calculateBottomPadding(),
            )
            .fillMaxWidth()
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
            contentPadding = PaddingValues(
                start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
                end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
            )
        ) { selectedType ->
            CollectionTypeContent(
                state = state,
                selectedType = selectedType,
                onEventSent = onEventSent
            )
        }

        IdVerificationSection(
            selected = state.idVerification,
            onSelected = { option ->
                onEventSent(OrderFulfilmentScreenContract.Event.IdVerificationChanged(option))
            },
            otherText = state.idVerificationOtherText,
            onOtherTextChange = { text ->
                onEventSent(OrderFulfilmentScreenContract.Event.IdVerificationOtherChanged(text))
            },
            contentPadding = PaddingValues(
                start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
                end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
            )
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
                isLoading = state.isLoading,
                modifier = Modifier,
                contentPadding = PaddingValues(bottom = Dimens.Space.medium)
            )
        }

        else -> {
            // No additional UI for STANDARD
        }
    }
}

// Placeholder card shown when there are no orders
@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun EmptyOrderPlaceholderCard(
    onLookupClick: () -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.medium),
    isLoading: Boolean = false,
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(contentPadding)
            .heightIn(min = 110.dp)
            .dashedBorder(shape = MaterialTheme.shapes.medium),
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
            searchState = SearchContract.State.empty(),
            onEventSent = {},
            onSearchEventSent = {},
            effectFlow = null,
            onOutcome = {},
        )
    }
}


