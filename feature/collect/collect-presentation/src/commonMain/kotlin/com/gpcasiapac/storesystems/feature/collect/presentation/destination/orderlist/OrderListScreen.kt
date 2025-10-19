package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowForward
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.CloudCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.Delete
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
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
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.CollectOrderItem
import com.gpcasiapac.storesystems.feature.collect.presentation.component.MBoltSearchBar
import com.gpcasiapac.storesystems.feature.collect.presentation.component.StickyBarDefaults
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.HeaderSection
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.MultiSelectConfirmDialog
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.OrderListToolbar
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.ToolbarFabContainer
import com.gpcasiapac.storesystems.feature.collect.presentation.search.SearchContract
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


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OrderListScreen(
    state: OrderListScreenContract.State,
    searchState: SearchContract.State,
    onEventSent: (event: OrderListScreenContract.Event) -> Unit,
    onSearchEventSent: (SearchContract.Event) -> Unit,
    effectFlow: Flow<OrderListScreenContract.Effect>?,
    onOutcome: (outcome: OrderListScreenContract.Effect.Outcome) -> Unit,
    searchEffectFlow: Flow<SearchContract.Effect>? = null,
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


    // Track FAB expanded/collapsed based on grid scroll direction
    var fabExpanded by remember { mutableStateOf(true) }
    LaunchedEffect(lazyGridState) {
        var previous = 0
        androidx.compose.runtime.snapshotFlow {
            // Combine index and offset into a monotonically increasing value as we scroll down
            lazyGridState.firstVisibleItemIndex * 100000 + lazyGridState.firstVisibleItemScrollOffset
        }.collect { current ->
            if (current > previous) {
                // Scrolling down: collapse to icon-only
                fabExpanded = false
            } else if (current < previous) {
                // Scrolling up: expand to show label
                fabExpanded = true
            }
            previous = current
        }
    }
    // Ensure FAB expands again when scrolling stops (idle state)
    LaunchedEffect(lazyGridState) {
        androidx.compose.runtime.snapshotFlow { lazyGridState.isScrollInProgress }
            .collectLatest { inProgress ->
                if (!inProgress) fabExpanded = true
            }
    }

    // Auto-scroll to prevent sticky header overlap when it appears
    LaunchedEffect(state.orders.isNotEmpty()) {
        if (state.orders.isNotEmpty()) {
            if (lazyGridState.firstVisibleItemIndex == 0 && lazyGridState.firstVisibleItemScrollOffset == 0) {
                // Scroll so that the sticky header sits at the top and doesn't cover content
                lazyGridState.animateScrollToItem(1)
            }
        }
    }
    val scope = rememberCoroutineScope()


    // Search bar state management
//    val searchBarState = rememberSearchBarState(initialValue = if (searchState.isSearchActive) SearchBarValue.Expanded else SearchBarValue.Collapsed)
    val searchBarState = rememberSearchBarState(initialValue = SearchBarValue.Collapsed)


    // Keep search bar animation in sync with SearchViewModel
    LaunchedEffect(searchState.isSearchActive) {
        scope.launch {
            if (searchState.isSearchActive) {
                searchBarState.animateToExpanded()
            } else {
                searchBarState.animateToCollapsed()
            }
        }
    }


    // Dialog state for multi-select confirmation
    val confirmDialogSpec =
        remember { mutableStateOf<OrderListScreenContract.Effect.ShowMultiSelectConfirmDialog?>(null) }
    // Parent-driven search confirmation dialog spec
    val searchConfirmDialogSpec = remember {
        mutableStateOf<com.gpcasiapac.storesystems.feature.collect.presentation.search.SearchContract.Effect.ShowMultiSelectConfirmDialog?>(
            null
        )
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

                is OrderListScreenContract.Effect.ShowMultiSelectConfirmDialog -> {
                    confirmDialogSpec.value = effect
                }

                is OrderListScreenContract.Effect.ShowSearchMultiSelectConfirmDialog -> {
                    // Adapt to SearchContract dialog spec for reuse of common dialog UI
                    searchConfirmDialogSpec.value =
                        com.gpcasiapac.storesystems.feature.collect.presentation.search.SearchContract.Effect.ShowMultiSelectConfirmDialog(
                            title = effect.title,
                            cancelLabel = effect.cancelLabel,
                            selectOnlyLabel = effect.selectOnlyLabel,
                            proceedLabel = effect.proceedLabel
                        )
                }
            }
        }
    }


    LaunchedEffect(searchEffectFlow) {
        searchEffectFlow?.collectLatest { effect ->
            when (effect) {
                is SearchContract.Effect.ShowMultiSelectConfirmDialog -> {
                    searchConfirmDialogSpec.value = effect
                }

                is SearchContract.Effect.ExpandSearchBar, is SearchContract.Effect.CollapseSearchBar -> {
                    // handled via searchState.isSearchActive syncing
                }
            }
        }
    }

    // Keyboard and focus controllers for IME dismissal on toolbar actions
    val focusManager = LocalFocusManager.current
    val keyboardController = LocalSoftwareKeyboardController.current
//    val exitAlwaysScrollBehavior =
//        exitAlwaysScrollBehavior(exitDirection = Bottom)
    Scaffold(
        //   modifier = Modifier.nestedScroll(exitAlwaysScrollBehavior),
        containerColor = MaterialTheme.colorScheme.surface,
        floatingActionButtonPosition = FabPosition.Center,
        floatingActionButton = {
            val hasDraft = state.isDraftBarVisible && state.existingDraftIdSet.isNotEmpty()

            ToolbarFabContainer(
                hasDraft = hasDraft,
                count = state.existingDraftIdSet.size,
                onNewTask = { onEventSent(OrderListScreenContract.Event.StartNewWorkOrderClicked) },
                onDelete = { onEventSent(OrderListScreenContract.Event.DraftBarDeleteClicked) },
                onView = { onEventSent(OrderListScreenContract.Event.DraftBarViewClicked) },
                modifier = Modifier
                //  .padding(bottom = Dimens.Space.medium, end = Dimens.Space.medium)
//                    .animateFloatingActionButton(
//                        visible = !state.isMultiSelectionEnabled,
//                        alignment = Alignment.BottomEnd
//                    )
            )
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
                secondaryAppBar = {
                    Surface(
                        color = MaterialTheme.colorScheme.secondary,
                        modifier = Modifier.fillMaxWidth()
                    ) {
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
                                onSearchEventSent(SearchContract.Event.SearchResultClicked(result))
                            },
                            onClearClick = {
                                onSearchEventSent(SearchContract.Event.ClearSearch)
                            },
                            searchResults = searchState.orderSearchSuggestionList.map { it.text },
                            searchOrderItems = searchState.searchResults,
                            isMultiSelectionEnabled = searchState.isMultiSelectionEnabled,
                            selectedOrderIdList = searchState.selectedOrderIdList,
                            isSelectAllChecked = searchState.isSelectAllChecked,
                            isRefreshing = state.isRefreshing,
                            onOpenOrder = { id ->
                                onEventSent(OrderListScreenContract.Event.OpenOrder(id))
                            },
                            onCheckedChange = { orderId, checked ->
                                onSearchEventSent(
                                    SearchContract.Event.OrderChecked(
                                        orderId = orderId,
                                        checked = checked
                                    )
                                )
                            },
                            onSelectAllToggle = { checked ->
                                onSearchEventSent(SearchContract.Event.SelectAll(checked))
                            },
                            onCancelSelection = {
                                onSearchEventSent(SearchContract.Event.CancelSelection)
                            },
                            onEnterSelectionMode = {
//                                focusManager.clearFocus(force = true)
//                                keyboardController?.hide()
                                onSearchEventSent(
                                    SearchContract.Event.ToggleSelectionMode(
                                        enabled = true
                                    )
                                )
                            },
                            onSelectClick = {
                                onEventSent(OrderListScreenContract.Event.ConfirmSearchSelection)
                            },
                            modifier = Modifier.fillMaxWidth(),
                            placeholderText = "Search by Order #, Name, Phone"
                        )
                    }
                }
            )
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
                    CollectOrderItem(
                        customerName = collectOrderState.customerName,
                        customerType = collectOrderState.customerType,
                        invoiceNumber = collectOrderState.invoiceNumber,
                        webOrderNumber = collectOrderState.webOrderNumber,
                        pickedAt = collectOrderState.pickedAt,
                        isLoading = state.isRefreshing,
                        contendPadding = PaddingValues(),
                        modifier = Modifier.padding(
                            start = Dimens.Space.medium,
                            top = Dimens.Space.medium,
                            bottom = Dimens.Space.small,
                            end = if (state.isMultiSelectionEnabled) 0.dp else Dimens.Space.medium
                        ),
                    )
                }
            }
        }

        // Confirmation dialog (main list)
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

        // Confirmation dialog (search)
        val searchSpec = searchConfirmDialogSpec.value
        if (searchSpec != null) {
            MultiSelectConfirmDialog(
                spec = OrderListScreenContract.Effect.ShowMultiSelectConfirmDialog(
                    title = searchSpec.title,
                    cancelLabel = searchSpec.cancelLabel,
                    selectOnlyLabel = searchSpec.selectOnlyLabel,
                    proceedLabel = searchSpec.proceedLabel
                ),
                onProceed = {
                    searchConfirmDialogSpec.value = null
                    onSearchEventSent(SearchContract.Event.ConfirmSelectionProceed)
                    // Also trigger proceed outcome on main VM to keep behavior
                    onEventSent(OrderListScreenContract.Event.ConfirmSelectionProceed)
                },
                onSelect = {
                    searchConfirmDialogSpec.value = null
                    onSearchEventSent(SearchContract.Event.ConfirmSelectionStay)
                },
                onCancel = {
                    searchConfirmDialogSpec.value = null
                    onSearchEventSent(SearchContract.Event.DismissConfirmSelectionDialog)
                },
                onDismissRequest = {
                    searchConfirmDialogSpec.value = null
                    onSearchEventSent(SearchContract.Event.DismissConfirmSelectionDialog)
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
            searchState = SearchContract.State.empty(),
            onEventSent = {},
            onSearchEventSent = {},
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


@Composable
private fun SingleFabContainer(
    hasDraft: Boolean,
    draftCount: Int,
    fabExpanded: Boolean,
    onNewTask: () -> Unit,
    onDelete: () -> Unit,
    onView: () -> Unit,
    modifier: Modifier = Modifier,
) {
    androidx.compose.material3.FloatingActionButton(
        onClick = { if (hasDraft) onView() else onNewTask() },
        modifier = modifier,
        shape = androidx.compose.material3.FloatingActionButtonDefaults.extendedFabShape,
        containerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
    ) {
        if (hasDraft) {
            DraftFabInlineContent(
                count = draftCount,
                onDelete = onDelete,
                onView = onView
            )
        } else {
            NewTaskFabContent(expanded = fabExpanded)
        }
    }
}

@Composable
private fun NewTaskFabContent(expanded: Boolean) {
    Row(
        modifier = Modifier.padding(horizontal = Dimens.Space.large),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small)
    ) {
        Icon(imageVector = Icons.Outlined.Add, contentDescription = "Start work order")
        if (expanded) {
            androidx.compose.material3.Text("New task")
        }
    }
}

@Composable
private fun DraftFabInlineContent(
    count: Int,
    onDelete: () -> Unit,
    onView: () -> Unit,
) {
    Row(
        modifier = Modifier.padding(horizontal = Dimens.Space.large),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
    ) {
        androidx.compose.material3.OutlinedIconButton(
            onClick = onDelete
        ) {
            Icon(
                imageVector = Icons.Outlined.Delete,
                contentDescription = "Delete drafts"
            )
        }

        androidx.compose.foundation.layout.Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            androidx.compose.material3.Text(
                text = "Collection in progress",
                style = MaterialTheme.typography.labelSmall,
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small)
            ) {
                androidx.compose.material3.Text(
                    text = "$count",
                    style = MaterialTheme.typography.titleLarge,
                )
                androidx.compose.material3.Text(
                    text = "order selected",
                )
            }
        }

        androidx.compose.material3.FilledIconButton(
            onClick = onView
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
                contentDescription = "View drafts"
            )
        }
    }
}

//
//@OptIn(ExperimentalMaterial3ExpressiveApi::class)
//@Composable
//private fun ToolbarFabContainer(
//    hasDraft: Boolean,
//    count: Int,
//    onNewTask: () -> Unit,
//    onDelete: () -> Unit,
//    onView: () -> Unit,
//    modifier: Modifier = Modifier,
//) {
//    if (!hasDraft) {
//        // Collapsed state: use a smaller FAB
//        androidx.compose.material3.SmallFloatingActionButton(
//            onClick = onNewTask,
//            modifier = modifier,
//        ) {
//            Icon(
//                imageVector = Icons.Outlined.Add,
//                contentDescription = "Start work order"
//            )
//        }
//    } else {
//        // Expanded state: toolbar with attached FAB and old button sizes
//        HorizontalFloatingToolbar(
//            expanded = true,
//            floatingActionButton = {
//                androidx.compose.material3.FloatingToolbarDefaults.VibrantFloatingActionButton(
//                    onClick = onNewTask,
//                ) {
//                    Icon(
//                        imageVector = Icons.Outlined.Add,
//                        contentDescription = "Start work order"
//                    )
//                }
//            },
//            modifier = modifier,
//            colors = androidx.compose.material3.FloatingToolbarDefaults.standardFloatingToolbarColors(
//                toolbarContainerColor = MaterialTheme.colorScheme.surfaceContainerHigh,
//            ),
//            expandedShadowElevation = 5.dp,
//        ) {
//            Row(
//                verticalAlignment = Alignment.CenterVertically,
//                horizontalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
//            ) {
//                androidx.compose.material3.OutlinedIconButton(
//                    modifier = Modifier
//                        .minimumInteractiveComponentSize()
//                        .size(
//                            androidx.compose.material3.IconButtonDefaults.extraSmallContainerSize(
//                                androidx.compose.material3.IconButtonDefaults.IconButtonWidthOption.Uniform
//                            )
//                        ),
//                    onClick = onDelete
//                ) {
//                    Icon(
//                        imageVector = Icons.Outlined.Delete,
//                        contentDescription = "Delete drafts"
//                    )
//                }
//
//                androidx.compose.foundation.layout.Column(
//                    modifier = Modifier.padding(horizontal = Dimens.Space.large),
//                    horizontalAlignment = Alignment.CenterHorizontally
//                ) {
//                    androidx.compose.material3.Text(
//                        text = "Collection in progress",
//                        style = MaterialTheme.typography.labelSmall,
//                    )
//                    Row(
//                        verticalAlignment = Alignment.CenterVertically,
//                        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small)
//                    ) {
//                        androidx.compose.material3.Text(
//                            text = "$count",
//                            style = MaterialTheme.typography.titleLarge,
//                        )
//                        androidx.compose.material3.Text(
//                            text = "order selected",
//                        )
//                    }
//                }
//
//                androidx.compose.material3.FilledIconButton(
//                    modifier = Modifier
//                        .minimumInteractiveComponentSize()
//                        .size(
//                            androidx.compose.material3.IconButtonDefaults.smallContainerSize(
//                                androidx.compose.material3.IconButtonDefaults.IconButtonWidthOption.Wide
//                            )
//                        ),
//                    onClick = onView
//                ) {
//                    Icon(
//                        imageVector = Icons.AutoMirrored.Outlined.ArrowForward,
//                        contentDescription = "View drafts"
//                    )
//                }
//            }
//        }
//    }
//}
