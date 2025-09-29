package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist


import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.input.rememberTextFieldState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ExitToApp
import androidx.compose.material.icons.filled.CloudCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Web
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import au.com.gpcasiapac.compose.collectappui.ui.components.FilterBar
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CheckboxOrderCard
import com.gpcasiapac.storesystems.feature.collect.presentation.components.HeaderSection
import com.gpcasiapac.storesystems.feature.collect.presentation.components.MBoltSearchBar
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
    val isSticky by remember {
        derivedStateOf {
            val firstVisible = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull()
            // sticky header is considered "stuck" when it's not the first visible item
            firstVisible?.index != 1
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
        snackbarHost = {
            SnackbarHost(snackbarHostState)
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
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.secondary)
                            .padding(
                                horizontal = Dimens.Space.medium,
                                vertical = Dimens.Space.small
                            )
                    ) {
                        MBoltSearchBar(
                            textFieldState = rememberTextFieldState(),
                            onSearch = {},
                            searchResults = listOf()
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
                    //TODO Refactor to use view model contracts and events.
                    FilterBar(
                        selectedFilters = listOf(),
                        phoneNumber = null,
                        onFilterToggle = { filter ->
//                            selectedFilters = if (selectedFilters.contains(filter)) {
//                                selectedFilters - filter
//                            } else {
//                                selectedFilters + filter
//                            }
                        },
                        onPhoneNumberClear = { },
                        onSelectAction = {
                            onEventSent(OrderListScreenContract.Event.ToggleSelectionMode(enabled = !state.isMultiSelectionEnabled))
                        },
                        contentPadding = PaddingValues(
                            horizontal = Dimens.Space.medium,
                            vertical = Dimens.Space.small
                        ),
                        modifier = Modifier
                            .then(
                                if (isSticky) {
                                    Modifier
                                } else {
                                    Modifier
                                        .background(
                                            MaterialTheme.colorScheme.surfaceContainerLow,
                                        )
                                        .border(
                                            width = 1.dp,
                                            color = MaterialTheme.colorScheme.outlineVariant,
                                            shape = MaterialTheme.shapes.small
                                        )
                                }
                            )
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
                            //TODO Use filter chips.
                            onEventSent(OrderListScreenContract.Event.ApplyFilters(listOf()))
                        }
                    )
                }
            }


        }
    }
}
