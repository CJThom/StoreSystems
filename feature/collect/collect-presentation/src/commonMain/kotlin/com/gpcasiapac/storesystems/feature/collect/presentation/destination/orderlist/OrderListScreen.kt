package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.CloudCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.collect.presentation.component.CollectOrderDetails
import com.gpcasiapac.storesystems.feature.collect.presentation.component.StickyBarDefaults
import com.gpcasiapac.storesystems.feature.collect.presentation.components.MBoltSearchBar
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.FilterBar
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.HeaderSection
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.MultiSelectBottomBar
import com.gpcasiapac.storesystems.foundation.component.CheckboxCard
import com.gpcasiapac.storesystems.foundation.component.GPCLogoTitle
import com.gpcasiapac.storesystems.foundation.component.MBoltAppBar
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    state: OrderListScreenContract.State,
    onEventSent: (event: OrderListScreenContract.Event) -> Unit,
    effectFlow: Flow<OrderListScreenContract.Effect>?,
    onOutcome: (outcome: OrderListScreenContract.Effect.Outcome) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val lazyGridState = rememberLazyGridState()
    val stickyHeaderScrollBehavior = StickyBarDefaults.liftOnScrollBehavior(
        lazyGridState = lazyGridState,
        stickyHeaderIndex = 1
    )
    
    // Search bar state management
    val searchBarState = rememberSearchBarState(
        initialValue = if (state.isSearchActive) SearchBarValue.Expanded else SearchBarValue.Collapsed
    )
    val textFieldState = rememberTextFieldState(initialText = state.searchText)
    
    // Monitor text changes and trigger search
    LaunchedEffect(textFieldState) {
        snapshotFlow { textFieldState.text.toString() }
            .collect { query ->
                if (query != state.searchText) {
                    onEventSent(OrderListScreenContract.Event.SearchTextChanged(query))
                }
            }
    }

    LaunchedEffect(effectFlow) {
        effectFlow?.collectLatest { effect ->
            when (effect) {
                is OrderListScreenContract.Effect.ShowToast ->
                    snackbarHostState.showSnackbar(
                        effect.message,
                        duration = SnackbarDuration.Short
                    )

                is OrderListScreenContract.Effect.ShowError ->
                    snackbarHostState.showSnackbar(effect.error, duration = SnackbarDuration.Long)

                is OrderListScreenContract.Effect.Outcome -> onOutcome(effect)
                is OrderListScreenContract.Effect.CopyToClipboard -> TODO()
                is OrderListScreenContract.Effect.Haptic -> TODO()
                is OrderListScreenContract.Effect.OpenDialer -> TODO()
                is OrderListScreenContract.Effect.ShowSnackbar -> TODO()
            }
        }
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        topBar = {
            MBoltAppBar(
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = { onEventSent(OrderListScreenContract.Event.Logout) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ExitToApp,
                            contentDescription = "Logout",
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle notifications */ }) {
                        Icon(
                            imageVector = Icons.Default.CloudCircle,
                            contentDescription = "History"
                        )
                    }
                    IconButton(onClick = { /* Handle more options */ }) {
                        Icon(
                            imageVector = Icons.Default.MoreVert,
                            contentDescription = "More options"
                        )
                    }
                },
                title = {
                    GPCLogoTitle("Collect")
                },
                actionBar = {
                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        MBoltSearchBar(
                            textFieldState = textFieldState,
                            searchBarState = searchBarState,
                            onSearch = { query ->
                                // Triggered when user presses enter/search button
                                onEventSent(OrderListScreenContract.Event.SearchTextChanged(query))
                            },
                            onExpandedChange = { isExpanded ->
                                onEventSent(OrderListScreenContract.Event.SearchActiveChanged(isExpanded))
                            },
                            onResultClick = { result ->
                                onEventSent(OrderListScreenContract.Event.SearchResultClicked(result))
                            },
                            onClearClick = {
                                onEventSent(OrderListScreenContract.Event.ClearSearch)
                            },
                            searchResults = state.orderSearchSuggestionList.map { it.text },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimens.Space.medium),
                            placeholderText = "Search by Order #, Name, Phone"
                        )

                    }
                }
            )
        },
        bottomBar = {
            AnimatedVisibility(
                visible = state.isMultiSelectionEnabled,
                enter = slideInVertically(initialOffsetY = { it }),
                exit = slideOutVertically(targetOffsetY = { it })
            ) {
                MultiSelectBottomBar(
                    selectedCount = state.selectedOrderIdList.size,
                    isSelectAllChecked = state.isSelectAllChecked,
                    onSelectAllToggle = { checked ->
                        onEventSent(OrderListScreenContract.Event.SelectAll(checked))
                    },
                    onCancelClick = {
                        onEventSent(OrderListScreenContract.Event.CancelSelection)
                    },
                    onSelectClick = {
                        onEventSent(OrderListScreenContract.Event.ConfirmSelection)
                    }
                )
            }
        },
    ) { padding ->
        LazyVerticalGrid(
            state = lazyGridState,
            columns = GridCells.Adaptive(Dimens.Adaptive.gridItemWidth),
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(
                start = padding.calculateStartPadding(LocalLayoutDirection.current),
                end = padding.calculateStartPadding(LocalLayoutDirection.current),
                bottom = padding.calculateBottomPadding()
            ),
        ) {
            item {
                HeaderSection(
                    ordersCount = state.orderCount,
                    modifier = Modifier,
                    isLoading = state.isRefreshing
                )
            }
            stickyHeader {
                FilterBar(
                    customerTypeFilterList = state.customerTypeFilterList,
                    scrollBehavior = stickyHeaderScrollBehavior,
                    isLoading = state.isRefreshing,
                    onToggleCustomerType = { type, checked ->
                        onEventSent(
                            OrderListScreenContract.Event.ToggleCustomerType(
                                type = type,
                                checked = checked
                            )
                        )
                    },
                    onSelectAction = {
                        onEventSent(
                            OrderListScreenContract.Event.ToggleSelectionMode(
                                enabled = !state.isMultiSelectionEnabled
                            )
                        )
                    }
                )
            }
            items(items = state.collectOrderStateList, key = { it.id }) { collectOrderState ->
                CheckboxCard(
                    modifier = Modifier.padding(
                        horizontal = Dimens.Space.medium,
                        vertical = Dimens.Space.small
                    ),
                    isCheckable = state.isMultiSelectionEnabled,
                    isChecked = state.selectedOrderIdList.contains(collectOrderState.id),
                    onClick = {
                        onEventSent(OrderListScreenContract.Event.OpenOrder(collectOrderState.id))
                    },
                    onCheckedChange = { isChecked ->
                        onEventSent(
                            OrderListScreenContract.Event.OrderChecked(
                                orderId = collectOrderState.id,
                                checked = isChecked
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
                        isLoading = state.isRefreshing,
                        contendPadding = PaddingValues(
                            start = Dimens.Space.medium,
                            top = Dimens.Space.medium,
                            bottom = Dimens.Space.medium,
                            end = if (state.isMultiSelectionEnabled) 0.dp else Dimens.Space.medium
                        ),
                    )
                }
            }
        }
    }
}

@Preview(
    name = "Order list",
    showBackground = true,
    backgroundColor = 0xFFF5F5F5L,
    widthDp = 360,
    heightDp = 720
)
@Composable
private fun OrderListScreenPreview(
    @PreviewParameter(OrderListScreenStateProvider::class) state: OrderListScreenContract.State
) {
    GPCTheme {
        OrderListScreen(
            state = state,
            onEventSent = {},
            effectFlow = null,
            onOutcome = {}
        )
    }
}
