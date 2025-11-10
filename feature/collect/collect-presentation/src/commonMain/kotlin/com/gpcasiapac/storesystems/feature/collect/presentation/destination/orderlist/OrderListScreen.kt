package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist


import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.scaleOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FabPosition
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SearchBarValue
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberSearchBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.common.feedback.haptic.HapticPerformer
import com.gpcasiapac.storesystems.common.feedback.sound.SoundPlayer
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.presentation.component.StickyBarDefaults
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.CollectOrderItem
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.HeaderSection
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.MultiSelectConfirmDialog
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.OrderListToolbar
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.component.ToolbarFabContainer
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.search.SearchComponent
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.search.SearchContract
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.search.SearchDestination
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.search.SearchViewModel
import com.gpcasiapac.storesystems.feature.collect.presentation.selection.SelectionContract
import com.gpcasiapac.storesystems.foundation.component.CheckboxCard
import com.gpcasiapac.storesystems.foundation.component.GPCLogoTitle
import com.gpcasiapac.storesystems.foundation.component.MBoltAppBar
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel


@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OrderListScreen(
    state: OrderListScreenContract.State,
    onEventSent: (event: OrderListScreenContract.Event) -> Unit,
    effectFlow: Flow<OrderListScreenContract.Effect>?,
    onOutcome: (outcome: OrderListScreenContract.Effect.Outcome) -> Unit,
    searchState: SearchContract.State,
    onSearchEventSent: (SearchContract.Event) -> Unit,
    searchEffectFlow: Flow<SearchContract.Effect>?,
    soundPlayer: SoundPlayer? = null,
    hapticPerformer: HapticPerformer? = null,
) {

    val snackbarHostState = remember { SnackbarHostState() }

    val scope = rememberCoroutineScope()

    // Search bar state management
    // val searchBarState = rememberSearchBarState(initialValue = SearchBarValue.Collapsed)

    val lazyGridState = rememberLazyGridState()
    val stickyHeaderScrollBehavior = StickyBarDefaults.liftOnScrollBehavior(
        lazyGridState = lazyGridState,
        stickyHeaderIndex = 1
    )

    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(
        canScroll = { !state.selection.isEnabled }
    )

    // When entering multi-select, smoothly expand the top app bar so it isn't hidden
    LaunchedEffect(state.selection.isEnabled) {
        if (state.selection.isEnabled) {
            val appBarState = scrollBehavior.state
            val anim = Animatable(appBarState.heightOffset)
            anim.animateTo(
                targetValue = 0f,
                animationSpec = tween(durationMillis = 300)
            ) {
                appBarState.heightOffset = value
                // Keep contentOffset reset so future nested scroll does not instantly re-collapse
                appBarState.contentOffset = 0f
            }
        }
    }


    // Track FAB expanded/collapsed based on grid scroll direction
    var fabExpanded by remember { mutableStateOf(true) }
    LaunchedEffect(lazyGridState) {
        var previous = 0
        snapshotFlow {
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
    // Ensure FAB expands again when scrolling stops (idle state), but wait briefly to avoid flicker
    LaunchedEffect(lazyGridState) {
        snapshotFlow { lazyGridState.isScrollInProgress }
            .collectLatest { inProgress ->
                if (!inProgress) {
                    delay(750)
                    // If scrolling resumed within the delay, this block would be cancelled (collectLatest)
                    fabExpanded = true
                }
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

    LaunchedEffect(effectFlow) {
        effectFlow?.collectLatest { effect ->
            when (effect) {
                is OrderListScreenContract.Effect.Outcome -> onOutcome(effect)
                is OrderListScreenContract.Effect.PlayHaptic -> {
                    hapticPerformer?.perform(effect.hapticEffect)
                }

                is OrderListScreenContract.Effect.PlaySound -> {
                    soundPlayer?.play(effect.soundEffect)
                }

                is OrderListScreenContract.Effect.ShowSnackbar -> {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            message = effect.message,
                            actionLabel = effect.actionLabel,
                            duration = effect.duration
                        )
                    }
                }

                is OrderListScreenContract.Effect.CollapseSearchBar -> {
                    onSearchEventSent(SearchContract.Event.CollapseSearchBar)
                }

                is OrderListScreenContract.Effect.ConfirmSearchSelection -> {
                    onSearchEventSent(SearchContract.Event.Selection(SelectionContract.Event.Confirm))
                }
                is OrderListScreenContract.Effect.CancelSearchSelection -> {
                    onSearchEventSent(SearchContract.Event.Selection(SelectionContract.Event.Cancel))
                }
            }
        }
    }


    LaunchedEffect(searchEffectFlow) {
        searchEffectFlow?.collectLatest { effect ->
            when (effect) {
                is SearchContract.Effect.Outcome.Back -> {}
                is SearchContract.Effect.Outcome.OrderClicked -> {
                    onEventSent(OrderListScreenContract.Event.OpenOrder(effect.invoiceNumber))
                }

                is SearchContract.Effect.Outcome.RequestConfirmationDialog -> {
                    onEventSent(OrderListScreenContract.Event.OnAcceptMultiSelectClicked(true))
                }

                else -> {}
            }
        }
    }


    Scaffold(
        containerColor = MaterialTheme.colorScheme.surface,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState)
        },
        floatingActionButtonPosition = FabPosition.End,
        floatingActionButton = {
            val hasDraft = state.isDraftBarVisible && state.selection.existing.isNotEmpty()

            AnimatedVisibility(
                visible = !state.selection.isEnabled,
                enter = fadeIn(animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()) +
                        scaleIn(
                            initialScale = 0.2f,
                            animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
                            transformOrigin = TransformOrigin(1f, 1f)
                        ),
                exit = fadeOut(animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()) +
                        scaleOut(
                            targetScale = 0.2f,
                            animationSpec = MaterialTheme.motionScheme.fastSpatialSpec(),
                            transformOrigin = TransformOrigin(1f, 1f)
                        )
            ) {
                ToolbarFabContainer(
                    hasDraft = hasDraft,
                    count = state.selection.existing.size,
                    onNewTask = { onEventSent(OrderListScreenContract.Event.StartNewWorkOrderClicked) },
                    onDelete = { onEventSent(OrderListScreenContract.Event.DraftBarDeleteClicked) },
                    onView = { onEventSent(OrderListScreenContract.Event.DraftBarViewClicked) },
                    expanded = fabExpanded,
//                    modifier = Modifier.animateFloatingActionButton(
//                        visible = !state.isMultiSelectionEnabled,
//                        alignment = Alignment.BottomEnd
//                    )
                )
            }
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
                    IconButton(onClick = {
                        onEventSent(OrderListScreenContract.Event.OpenHistory)
                    }) {
                        Icon(
                            imageVector = Icons.Filled.History,
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
                        AnimatedVisibility(
                            visible = !state.selection.isEnabled,
                            enter = fadeIn(animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()) + expandVertically(
                                animationSpec = tween(250)
                            ),
                            exit = shrinkVertically(animationSpec = tween(200)) + fadeOut(
                                animationSpec = MaterialTheme.motionScheme.fastEffectsSpec()
                            )
                        ) {
                            SearchComponent(
                                state = searchState,
                                onEventSent = onSearchEventSent,
                                effectFlow = searchEffectFlow,
                                collapsedColors = SearchBarDefaults.colors(containerColor = MaterialTheme.colorScheme.surface),
                                placeholderText = "Search by Order #, Name, Phone"
                            )
                        }
                    }
                }
            )
        }
    ) { padding ->

        LazyVerticalGrid(
            state = lazyGridState,
            columns = GridCells.Adaptive(Dimens.Adaptive.gridItemWidth),
            modifier = Modifier
                .padding(top = padding.calculateTopPadding())
                .fillMaxSize()
                .nestedScroll(scrollBehavior.nestedScrollConnection),
            contentPadding = PaddingValues(
                start = padding.calculateStartPadding(LocalLayoutDirection.current),
                end = padding.calculateStartPadding(LocalLayoutDirection.current),
                bottom = padding.calculateBottomPadding()
            ),
        ) {
            item {
                AnimatedVisibility(
                    visible = !state.selection.isEnabled,
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
                    isMultiSelectionEnabled = state.selection.isEnabled,
                    customerTypeFilterList = buildSet {
                        if (state.filters.showB2B) add(CustomerType.B2B)
                        if (state.filters.showB2C) add(CustomerType.B2C)
                    },
                    selectedCount = state.selection.selected.size,
                    isSelectAllChecked = state.selection.isAllSelected,
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
                            OrderListScreenContract.Event.Selection(
                                SelectionContract.Event.ToggleMode(true)
                            )
                        )
                    },
                    onSelectAllToggle = { checked ->
                        onEventSent(
                            OrderListScreenContract.Event.Selection(
                                SelectionContract.Event.SelectAll(checked)
                            )
                        )
                    },
                    onCancelClick = {
                        onEventSent(
                            OrderListScreenContract.Event.Selection(
                                SelectionContract.Event.Cancel
                            )
                        )
                    },
                    onSelectClick = {
                        onEventSent(OrderListScreenContract.Event.OnAcceptMultiSelectClicked(false))
                    },
                )
            }
            items(
                items = state.orders,
                key = { it.invoiceNumber.value }) { collectOrderState ->
                CheckboxCard(
                    modifier = Modifier
                        .padding(
                            horizontal = Dimens.Space.medium,
                            vertical = Dimens.Space.small
                        )
                        .animateItem()
                        .animateContentSize(),
                    isCheckable = state.selection.isEnabled,
                    isChecked = state.selection.selected.contains(collectOrderState.invoiceNumber),
                    onClick = {
                        onEventSent(OrderListScreenContract.Event.OpenOrder(collectOrderState.invoiceNumber))
                    },
                    onCheckedChange = { isChecked ->
                        onEventSent(
                            OrderListScreenContract.Event.Selection(
                                SelectionContract.Event.SetItemChecked(
                                    id = collectOrderState.invoiceNumber,
                                    checked = isChecked
                                )
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
                            end = if (state.selection.isEnabled) 0.dp else Dimens.Space.medium
                        ),
                    )
                }
            }
        }

    }

    Dialogs(state.dialog)
}

@Composable
private fun Dialogs(dialog: OrderListScreenContract.Dialog?) {
    when (dialog) {
        is OrderListScreenContract.Dialog.SearchMultiSelectConfirm -> {
            MultiSelectConfirmDialog(
                title = dialog.title,
                cancelLabel = dialog.onCancel.label.value,
                selectOnlyLabel = dialog.onSelect.label.value,
                proceedLabel = dialog.onProceed.label.value,
                onProceed = dialog.onProceed.action,
                onSelect = dialog.onSelect.action,
                onCancel = dialog.onCancel.action,
                onDismissRequest = dialog.onCancel.action
            )
        }

        null -> {}
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
            onOutcome = {},
            searchState = SearchContract.State.empty(),
            onSearchEventSent = {},
            searchEffectFlow = null,
        )
    }
}
