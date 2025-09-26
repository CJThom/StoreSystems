package com.gpcasiapac.storesystems.feature.collect.presentation.orders

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberTopAppBarState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import au.com.gpcasiapac.compose.collectappui.ui.components.FilterBar
import com.gpcasiapac.storesystems.feature.collect.presentation.components.CheckboxOrderCard
import com.gpcasiapac.storesystems.feature.collect.presentation.components.HeaderSection
import com.gpcasiapac.storesystems.feature.collect.presentation.components.MBoltSearchBar
import com.gpcasiapac.storesystems.foundation.component.GPCLogoTitle
import com.gpcasiapac.storesystems.foundation.component.MBoltAppBar
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.MBoltIcons
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    state: OrdersScreenContract.State,
    onEventSent: (event: OrdersScreenContract.Event) -> Unit,
    effectFlow: Flow<OrdersScreenContract.Effect>,
    onOutcome: (outcome: OrdersScreenContract.Effect.Outcome) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val scrollBehavior = TopAppBarDefaults.enterAlwaysScrollBehavior(rememberTopAppBarState())
    val lazyListState = rememberLazyListState()
    var selectedFilters by remember { mutableStateOf(listOf("B2B", "B2C")) }
    var selectedOrders by remember { mutableStateOf(emptySet<String>()) }
    val isSticky by remember {
        derivedStateOf {
            val firstVisible = lazyListState.layoutInfo.visibleItemsInfo.firstOrNull()
            // sticky header is considered "stuck" when it's not the first visible item
            firstVisible?.index != 1
        }
    }


    LaunchedEffect(effectFlow) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is OrdersScreenContract.Effect.ShowToast ->
                    snackbarHostState.showSnackbar(
                        effect.message,
                        duration = SnackbarDuration.Short
                    )

                is OrdersScreenContract.Effect.ShowError ->
                    snackbarHostState.showSnackbar(effect.error, duration = SnackbarDuration.Long)

                is OrdersScreenContract.Effect.Outcome -> onOutcome(effect)
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
                        ordersCount = state.orders.size,
                        modifier = Modifier.padding(horizontal = Dimens.Space.medium)
                    )
                }
                stickyHeader {
                    FilterBar(
                        selectedFilters = selectedFilters,
                        phoneNumber = null,
                        onFilterToggle = { filter ->
                            selectedFilters = if (selectedFilters.contains(filter)) {
                                selectedFilters - filter
                            } else {
                                selectedFilters + filter
                            }
                        },
                        onPhoneNumberClear = { },
                        onSelectAction = {
                            onEventSent(OrdersScreenContract.Event.MultiSelectionChanged(isEnabled = !state.isMultiSelectionEnabled))
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
                items(state.orders) { order ->
                    CheckboxOrderCard(
                        modifier = Modifier.padding(horizontal = Dimens.Space.medium),
                        isSelectable = state.isMultiSelectionEnabled,
                        customerName = order.customerName,
                        orderDetails = {

                        },
                        isBusiness = order.isBusiness,
                        deliveryTime = order.deliveryTime,
                        isSelected = selectedOrders.contains(order.customerName),
                        onSelectionChanged = { isSelected ->
                            selectedOrders = if (isSelected) {
                                selectedOrders + order.customerName
                            } else {
                                selectedOrders - order.customerName
                            }
                        }
                    )
                }
            }


        }
    }
}
