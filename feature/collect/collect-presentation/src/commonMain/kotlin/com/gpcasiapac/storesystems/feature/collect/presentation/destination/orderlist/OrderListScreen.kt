package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist


import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideOutVertically
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.CloudCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.FloatingToolbarDefaults.exitAlwaysScrollBehavior
import androidx.compose.material3.FloatingToolbarExitDirection.Companion.Bottom
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.collect.presentation.component.CollectOrderDetails
import com.gpcasiapac.storesystems.feature.collect.presentation.component.StickyBarDefaults
import com.gpcasiapac.storesystems.feature.collect.presentation.components.MBoltSearchBar
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.FilterBar
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.HeaderSection
import com.gpcasiapac.storesystems.foundation.component.CheckboxCard
import com.gpcasiapac.storesystems.foundation.component.GPCLogoTitle
import com.gpcasiapac.storesystems.foundation.component.MBoltAppBar
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.DraftBottomBar
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.MultiSelectConfirmDialog
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.MultiSelectTopBar
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.OrderListToolbar

//DraftBottomBar(
//count = state.existingDraftIdSet.size,
//onDelete = { onEventSent(OrderListScreenContract.Event.DraftBarDeleteClicked) },
//onView = { onEventSent(OrderListScreenContract.Event.DraftBarViewClicked) }
//)
@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OrderListScreen(
    state: OrderListScreenContract.State,
    onEventSent: (event: OrderListScreenContract.Event) -> Unit,
    effectFlow: Flow<OrderListScreenContract.Effect>?,
    onOutcome: (outcome: OrderListScreenContract.Effect.Outcome) -> Unit,
) {
    // Shared animation flags and timings
//    val multiFlags = rememberMultiSelectTransitionFlags(state.isMultiSelectionEnabled)
//    val timings = DefaultFabBarTimings
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val lazyGridState = rememberLazyGridState()
    val stickyHeaderScrollBehavior = StickyBarDefaults.liftOnScrollBehavior(
        lazyGridState = lazyGridState,
        stickyHeaderIndex = 1
    )
    val scope = rememberCoroutineScope()


    // Search bar state management
    val searchBarState =
        rememberSearchBarState(initialValue = if (state.isSearchActive) SearchBarValue.Expanded else SearchBarValue.Collapsed)

    // Dialog state for multi-select confirmation
    val confirmDialogSpec =
        remember { mutableStateOf<OrderListScreenContract.Effect.ShowMultiSelectConfirmDialog?>(null) }

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
                is OrderListScreenContract.Effect.CollapseSearchBar -> {
                    scope.launch {
                        searchBarState.animateToCollapsed()
                    }
                }

                is OrderListScreenContract.Effect.ExpandSearchBar -> {
                    scope.launch {
                        searchBarState.animateToExpanded()
                    }
                }

                is OrderListScreenContract.Effect.ShowMultiSelectConfirmDialog -> {
                    confirmDialogSpec.value = effect
                }
            }
        }
    }

    val exitAlwaysScrollBehavior =
        exitAlwaysScrollBehavior(exitDirection = Bottom)
    Scaffold(
        modifier = Modifier.nestedScroll(exitAlwaysScrollBehavior),
        containerColor = MaterialTheme.colorScheme.surface,
//        snackbarHost = {
//            SnackbarHost(snackbarHostState)
//        },
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
                secondaryAppBar = {
                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        MBoltSearchBar(
                            query = state.searchText,
                            onQueryChange = { query ->
                                onEventSent(OrderListScreenContract.Event.SearchTextChanged(query))
                            },
                            searchBarState = searchBarState,
                            onSearch = { query ->
                                onEventSent(OrderListScreenContract.Event.SearchTextChanged(query))
                            },
                            onExpandedChange = { isExpanded ->
                                onEventSent(
                                    OrderListScreenContract.Event.SearchOnExpandedChange(
                                        isExpanded
                                    )
                                )
                            },
                            onBackPressed = {
                                onEventSent(OrderListScreenContract.Event.SearchBarBackPressed)
                            },
                            onResultClick = { result ->
                                onEventSent(OrderListScreenContract.Event.SearchResultClicked(result))
                            },
                            onClearClick = {
                                onEventSent(OrderListScreenContract.Event.ClearSearch)
                            },
                            searchResults = state.orderSearchSuggestionList.map { it.text },
                            searchOrderItems = state.searchResults,
                            isMultiSelectionEnabled = state.isMultiSelectionEnabled,
                            selectedOrderIdList = state.selectedOrderIdList,
                            isSelectAllChecked = state.isSelectAllChecked,
                            isRefreshing = state.isRefreshing,
                            onOpenOrder = { id ->
                                onEventSent(OrderListScreenContract.Event.OpenOrder(id))
                            },
                            onCheckedChange = { orderId, checked ->
                                onEventSent(
                                    OrderListScreenContract.Event.OrderChecked(
                                        orderId = orderId,
                                        checked = checked
                                    )
                                )
                            },
                            onSelectAllToggle = { checked ->
                                onEventSent(OrderListScreenContract.Event.SelectAll(checked))
                            },
                            onCancelSelection = {
                                onEventSent(OrderListScreenContract.Event.CancelSelection)
                            },
                            onEnterSelectionMode = {
                                onEventSent(
                                    OrderListScreenContract.Event.ToggleSelectionMode(
                                        enabled = true
                                    )
                                )
                            },
                            onSelectClick = {
                                onEventSent(OrderListScreenContract.Event.ConfirmSelection)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholderText = "Search by Order #, Name, Phone"
                        )
                    }
                }
            )
        },
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
        },
        bottomBar = {
            // Switch between Multi-select, Draft bar, or none using AnimatedContent
            val bottomBarKey = when {
                state.isMultiSelectionEnabled -> "NONE"
                state.isDraftBarVisible -> "DRAFT"
                else -> "NONE"
            }
            AnimatedContent(
                targetState = bottomBarKey,
                transitionSpec = {
                    ContentTransform(
                        targetContentEnter = androidx.compose.animation.slideInVertically(
                            initialOffsetY = { it }
                        ),
                        initialContentExit = slideOutVertically(
                            targetOffsetY = { it }
                        )
                    )
                },
                label = "BottomBarTransition"
            ) { key ->
                when (key) {

                    "DRAFT" -> {
                        DraftBottomBar(
                            count = state.existingDraftIdSet.size,
                            onDelete = { onEventSent(OrderListScreenContract.Event.DraftBarDeleteClicked) },
                            onView = { onEventSent(OrderListScreenContract.Event.DraftBarViewClicked) }
                        )
                    }

                    else -> {
                        // No bottom bar
                    }
                }
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
                AnimatedVisibility(
                    visible = !state.isMultiSelectionEnabled,
                    enter = expandVertically(animationSpec = tween(250)) + fadeIn(),
                    exit = shrinkVertically(animationSpec = tween(200)) + fadeOut()
                ) {
                    HeaderSection(
                        ordersCount = state.orderCount,
                        modifier = Modifier,
                        isLoading = state.isRefreshing
                    )
                }
            }
            stickyHeader {
                    OrderListToolbar(
                        isMultiSelectionEnabled = state.isMultiSelectionEnabled,
                        customerTypeFilterList = state.customerTypeFilterList,
                        selectedCount = state.selectedOrderIdList.size,
                        isSelectAllChecked = state.isSelectAllChecked,
                        isLoading = state.isRefreshing,
                        scrollBehavior = stickyHeaderScrollBehavior,
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
                                    enabled = true
                                )
                            )
                        },
                        onSelectAllToggle = { checked ->
                            onEventSent(OrderListScreenContract.Event.SelectAll(checked))
                        },
                        onCancelClick = {
                            onEventSent(OrderListScreenContract.Event.CancelSelection)
                        },
                        onSelectClick = {
                            onEventSent(OrderListScreenContract.Event.ConfirmSelection)
                        },
                    )
                }
            items(
                items = state.orders,
                key = { it.invoiceNumber }) { collectOrderState ->
                CheckboxCard(
                    modifier = Modifier
                        .padding(
                            horizontal = Dimens.Space.medium,
                            vertical = Dimens.Space.small
                        )
                        .animateItem()
                        .animateContentSize(),
                    isCheckable = state.isMultiSelectionEnabled,
                    isChecked = state.selectedOrderIdList.contains(collectOrderState.invoiceNumber),
                    onClick = {
                        onEventSent(OrderListScreenContract.Event.OpenOrder(collectOrderState.invoiceNumber))
                    },
                    onCheckedChange = { isChecked ->
                        onEventSent(
                            OrderListScreenContract.Event.OrderChecked(
                                orderId = collectOrderState.invoiceNumber,
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


        // Confirmation dialog
        val spec = confirmDialogSpec.value
        if (spec != null) {
            MultiSelectConfirmDialog(
                spec = spec,
                onProceed = {
                    confirmDialogSpec.value = null
                    onEventSent(OrderListScreenContract.Event.ConfirmSelectionProceed)
                },
                onSelect = {
                    confirmDialogSpec.value = null
                    onEventSent(OrderListScreenContract.Event.ConfirmSelectionStay)
                },
                onCancel = {
                    confirmDialogSpec.value = null
                    onEventSent(OrderListScreenContract.Event.DismissConfirmSelectionDialog)
                },
                onDismissRequest = {
                    confirmDialogSpec.value = null
                    onEventSent(OrderListScreenContract.Event.DismissConfirmSelectionDialog)
                }
            )
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
fun OrderListScreenPreview(
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


// ---------------- Animation helpers (FAB <-> Bottom Bar coordination) ----------------
private data class MultiSelectTransitionFlags(
    val isEnteringMulti: Boolean,
    val isExitingMulti: Boolean,
)

@Composable
private fun rememberMultiSelectTransitionFlags(isMultiSelectionEnabled: Boolean): MultiSelectTransitionFlags {
    var previous by androidx.compose.runtime.remember {
        mutableStateOf(
            isMultiSelectionEnabled
        )
    }
    val entering = !previous && isMultiSelectionEnabled
    val exiting = previous && !isMultiSelectionEnabled
    androidx.compose.runtime.LaunchedEffect(isMultiSelectionEnabled) {
        previous = isMultiSelectionEnabled
    }
    return MultiSelectTransitionFlags(entering, exiting)
}

private data class FabBarTimings(
    val fabEnterDuration: Int = 180,
    val fabExitDuration: Int = 120,
    val barEnterDuration: Int = 250,
    val barExitDuration: Int = 200,
    val staggerGap: Int = 50,
    // Buffer so FAB waits even after bar fully exits (layout settles)
    val extraDelayAfterBarExitForFab: Int = 200,
    // Buffer so BAR waits even after fab fully exits (avoid FAB moving while BAR enters)
    val extraDelayAfterFabExitForBar: Int = 400,
)

private val DefaultFabBarTimings = FabBarTimings()

private fun fabEnterSpec(
    flags: MultiSelectTransitionFlags,
    t: FabBarTimings,
): androidx.compose.animation.EnterTransition {
    val delay =
        if (flags.isExitingMulti) t.barExitDuration + t.staggerGap + t.extraDelayAfterBarExitForFab else 0
    return androidx.compose.animation.scaleIn(
        animationSpec = tween(
            durationMillis = t.fabEnterDuration,
            delayMillis = delay
        ),
        transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 1f)
    ) + androidx.compose.animation.fadeIn(
        animationSpec = tween(
            durationMillis = t.fabEnterDuration,
            delayMillis = delay
        )
    )
}

private fun fabExitSpec(
    t: FabBarTimings,
): androidx.compose.animation.ExitTransition {
    return androidx.compose.animation.scaleOut(
        animationSpec = tween(durationMillis = t.fabExitDuration),
        transformOrigin = androidx.compose.ui.graphics.TransformOrigin(0.5f, 1f)
    ) + androidx.compose.animation.fadeOut(
        animationSpec = tween(durationMillis = t.fabExitDuration)
    )
}

private fun barEnterSpec(
    flags: MultiSelectTransitionFlags,
    t: FabBarTimings,
): androidx.compose.animation.EnterTransition {
    val delay =
        if (flags.isEnteringMulti) t.fabExitDuration + t.staggerGap + t.extraDelayAfterFabExitForBar else 0
    return androidx.compose.animation.expandVertically(
        expandFrom = Alignment.Bottom,
        animationSpec = tween(
            durationMillis = t.barEnterDuration,
            delayMillis = delay
        )
    ) + androidx.compose.animation.slideInVertically(
        initialOffsetY = { it },
        animationSpec = tween(
            durationMillis = t.barEnterDuration,
            delayMillis = delay
        )
    )
}

private fun barExitSpec(
    t: FabBarTimings,
): androidx.compose.animation.ExitTransition {
    return androidx.compose.animation.shrinkVertically(
        shrinkTowards = Alignment.Bottom,
        animationSpec = tween(
            durationMillis = t.barExitDuration
        )
    ) + slideOutVertically(
        targetOffsetY = { it },
        animationSpec = tween(
            durationMillis = t.barExitDuration
        )
    )
}
