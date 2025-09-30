package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.CloudCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.outlined.Receipt
import androidx.compose.material.icons.outlined.Web
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import com.gpcasiapac.storesystems.feature.collect.presentation.components.StickyBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import com.gpcasiapac.storesystems.feature.collect.presentation.components.FilterBar
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CheckboxOrderCard
import com.gpcasiapac.storesystems.feature.collect.presentation.components.HeaderSection
import com.gpcasiapac.storesystems.feature.collect.presentation.components.MBoltSearchBar
import com.gpcasiapac.storesystems.feature.collect.presentation.components.MultiSelectBottomBar
import com.gpcasiapac.storesystems.feature.collect.presentation.components.OrderDetailRow
import com.gpcasiapac.storesystems.foundation.component.GPCLogoTitle
import com.gpcasiapac.storesystems.foundation.component.MBoltAppBar
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

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
    val lazyListState = rememberLazyListState()
    val stickyHeaderScrollBehavior = StickyBarDefaults.liftOnScrollBehavior(lazyListState, stickyHeaderIndex = 1)


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
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        bottomBar = {
            if (state.isMultiSelectionEnabled) {
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
        topBar = {
            MBoltAppBar(
                scrollBehavior = scrollBehavior,
                navigationIcon = {
                    IconButton(onClick = { /* Handle menu */ }) {
                        Icon(
                            Icons.AutoMirrored.Outlined.ExitToApp,
                            contentDescription = "Menu",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* Handle notifications */ }) {
                        Icon(
                            Icons.Default.CloudCircle,
                            contentDescription = "History",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                    IconButton(onClick = { /* Handle more options */ }) {
                        Icon(
                            Icons.Default.MoreVert,
                            contentDescription = "More options",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                },
                title = {
                    GPCLogoTitle("Collect")
                },
                actionBar = {
                    Box(
                        contentAlignment = Alignment.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(MaterialTheme.colorScheme.secondary)
                            .padding(
                                horizontal = Dimens.Space.medium,
                                vertical = Dimens.Space.small
                            )
                    ) {
                        MBoltSearchBar(
                            textFieldState = rememberTextFieldState(initialText = state.searchText),
                            onSearch = { query ->
                                onEventSent(OrderListScreenContract.Event.SearchTextChanged(query))
                                onEventSent(OrderListScreenContract.Event.SearchActiveChanged(false))
                            },
                            searchResults = state.orderSearchSuggestionList.map { it.text }
                        )

                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()

        ) {
            // Order List
            LazyColumn(
                state = lazyListState,
                modifier = Modifier
                    .nestedScroll(scrollBehavior.nestedScrollConnection)
                    .fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium),
                contentPadding = PaddingValues(vertical = Dimens.Space.medium)
            ) {
                item {
                    // Header Section
                    HeaderSection(
                        ordersCount = state.orderCount,
                        modifier = Modifier.padding(horizontal = Dimens.Space.medium)
                    )
                }
                stickyHeader {
                    FilterBar(
                        scrollBehavior = stickyHeaderScrollBehavior,
                        selectedFilters = state.customerTypeFilterList.map { it.name },
                        additionalFilters = state.appliedFilterChipList,
                        onFilterToggle = { filter ->
                            val customerType = when (filter) {
                                "B2B" -> CustomerType.B2B
                                "B2C" -> CustomerType.B2C
                                else -> return@FilterBar
                            }
                            val isCurrentlySelected = state.customerTypeFilterList.contains(customerType)
                            onEventSent(OrderListScreenContract.Event.ToggleCustomerType(customerType, !isCurrentlySelected))
                        },
                        onAdditionalFilterRemove = { filterChip ->
                            onEventSent(OrderListScreenContract.Event.RemoveFilterChip(filterChip))
                        },
                        onSelectAction = {
                            onEventSent(OrderListScreenContract.Event.ToggleSelectionMode(enabled = !state.isMultiSelectionEnabled))
                        },
                        contentPadding = PaddingValues(
                            horizontal = Dimens.Space.medium,
                            vertical = Dimens.Space.small
                        ),
                    )

                }
                items(state.orderList) { order ->
                    CheckboxOrderCard(
                        modifier = Modifier.padding(horizontal = Dimens.Space.medium),
                        isSelectable = state.isMultiSelectionEnabled,
                        customerName = if (order.customerType == CustomerType.B2B) order.customer.accountName
                            ?: "" else order.customer.fullName,
                        orderDetails = {
                            OrderDetailRow(
                                modifier = Modifier.weight(1f),
                                text = order.invoiceNumber,
                                icon = Icons.Outlined.Receipt,
                            )
                            order.webOrderNumber?.let {
                                OrderDetailRow(
                                    modifier = Modifier.weight(1f),
                                    text = it,
                                    icon = Icons.Outlined.Web,
                                )
                            }

                        },
                        isBusiness = order.customerType == CustomerType.B2B,
                        deliveryTime = order.pickedAt.toString(),
                        isSelected = state.selectedOrderIdList.contains(order.id),
                        onSelectionChanged = { isSelected ->
                            onEventSent(OrderListScreenContract.Event.OrderChecked(order.id, isSelected))
                        },
                        onClick = {
                            onEventSent(OrderListScreenContract.Event.OpenOrder(order.id))
                        }
                    )
                }
            }
        }
    }
}
