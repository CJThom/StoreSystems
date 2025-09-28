package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.layout.navigationBars
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material.icons.outlined.BackHand
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.zIndex
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.Order
import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSearchSuggestionType
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.FilterChip as UiFilterChip
import com.gpcasiapac.storesystems.feature.collect.presentation.util.displayName
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    state: OrderListScreenContract.State,
    onEventSent: (event: OrderListScreenContract.Event) -> Unit,
    effectFlow: Flow<OrderListScreenContract.Effect>?,
    onOutcome: (outcome: OrderListScreenContract.Effect.Outcome) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

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

                is OrderListScreenContract.Effect.ShowSnackbar ->
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        actionLabel = effect.actionLabel,
                        duration = if (effect.persistent) SnackbarDuration.Indefinite else SnackbarDuration.Short
                    )

                is OrderListScreenContract.Effect.Haptic -> Unit
                is OrderListScreenContract.Effect.OpenDialer -> Unit
                is OrderListScreenContract.Effect.CopyToClipboard -> Unit

                is OrderListScreenContract.Effect.Outcome -> onOutcome(effect)
            }
        }
    }

    Scaffold(
        topBar = { OrderListTopBar(state = state, onEventSent = onEventSent) },
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        },
        bottomBar = {
            if (state.isMultiSelectionEnabled) {
                SelectionBottomBar(
                    checked = state.isSelectAllChecked,
                    onCheckedChange = { checked -> onEventSent(OrderListScreenContract.Event.SelectAll(checked)) },
                    selectedCount = state.selectedOrderIdList.size,
                    onCancel = { onEventSent(OrderListScreenContract.Event.CancelSelection) },
                    onConfirm = { onEventSent(OrderListScreenContract.Event.ConfirmSelection) }
                )
            }
        }
    ) { padding ->
        // Overlay layout: search bar floats above the list (like a dropdown)
        Box(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize()
        ) {
            // Main content behind the search bar
            when {
                state.isLoading -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                state.error != null -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Top
                    ) {
                        Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                            Text(
                                text = state.error,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                modifier = Modifier
                            )
                        }
                    }
                }

                else -> {
                    LazyColumn(
                        contentPadding = PaddingValues(
                            top = SearchBarDefaults.InputFieldHeight + 16.dp,
                            start = 16.dp,
                            end = 16.dp,
                            bottom = 16.dp
                        )
                    ) {
                        // Filter bar as the first row
                        item {
                            FilterBar(state = state, onEventSent = onEventSent)
                        }
                        items(state.filteredOrderList) { order ->
                            val isChecked = state.selectedOrderIdList.contains(order.id)
                            OrderCard(
                                order = order,
                                showCheckbox = state.isMultiSelectionEnabled,
                                checked = isChecked,
                                onCheckedChange = { checked ->
                                    onEventSent(
                                        OrderListScreenContract.Event.OrderChecked(order.id, checked)
                                    )
                                },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable {
                                        if (state.isMultiSelectionEnabled) {
                                            onEventSent(
                                                OrderListScreenContract.Event.OrderChecked(order.id, !isChecked)
                                            )
                                        } else {
                                            onEventSent(
                                                OrderListScreenContract.Event.OpenOrder(order.id)
                                            )
                                        }
                                    }
                                    .padding(vertical = 6.dp)
                            )
                        }
                    }
                }
            }

            // DockedSearchBar overlaid on top of the list
            val onActiveChange: (Boolean) -> Unit = { active ->
                onEventSent(OrderListScreenContract.Event.SearchActiveChanged(active))
            }
            val colors1 = SearchBarDefaults.colors()


            // Track focus of the input field and collapse search when focus is lost
            val inputInteraction = remember { MutableInteractionSource() }
            val isFocused = inputInteraction.collectIsFocusedAsState().value
            LaunchedEffect(isFocused, state.isSearchActive) {
                if (!isFocused && state.isSearchActive) {
                    onEventSent(OrderListScreenContract.Event.SearchActiveChanged(false))
                }
            }

            // Show suggestions only when there are results
            val hasSuggestions = state.orderSearchSuggestionList.isNotEmpty()
            val expanded = state.isSearchActive && hasSuggestions

            // Click outside to collapse: transparent scrim that sits above the list
            if (expanded) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .zIndex(0.5f)
                        .clickable(
                            indication = null,
                            interactionSource = remember { MutableInteractionSource() }
                        ) {
                            onEventSent(OrderListScreenContract.Event.SearchActiveChanged(false))
                        }
                )
            }

            // Use built-in DockedSearchBar expansion and its content slot for suggestions
            DockedSearchBar(
                inputField = {
                    SearchBarDefaults.InputField(
                        query = state.searchText,
                        onQueryChange = {
                            onEventSent(
                                OrderListScreenContract.Event.SearchTextChanged(
                                    it
                                )
                            )
                        },
                        onSearch = {
                            onEventSent(
                                OrderListScreenContract.Event.SearchActiveChanged(
                                    false
                                )
                            )
                        },
                        expanded = state.isSearchActive,
                        onExpandedChange = onActiveChange,
                        placeholder = { Text("Search orders…") },
                        leadingIcon = { Icon(Icons.Outlined.Search, contentDescription = null) },
                        trailingIcon = {
                            if (state.searchText.isNotEmpty()) {
                                IconButton(onClick = { onEventSent(OrderListScreenContract.Event.ClearSearch) }) {
                                    Icon(Icons.Outlined.Close, contentDescription = "Clear search")
                                }
                            }
                        },
                        colors = colors1.inputFieldColors,
                        interactionSource = inputInteraction,
                        modifier = Modifier.fillMaxWidth()
                    )
                },
                expanded = expanded,
                onExpandedChange = onActiveChange,
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(horizontal = 16.dp, vertical = 8.dp)
                    .zIndex(1f),
                shape = SearchBarDefaults.dockedShape,
                colors = colors1,
                tonalElevation = SearchBarDefaults.TonalElevation,
                shadowElevation = SearchBarDefaults.ShadowElevation,
            ) {
                // Built-in suggestions/results table
                LazyColumn(modifier = Modifier.fillMaxWidth()) {
                    items(state.orderSearchSuggestionList.take(4)) { s ->
                        SuggestionRow(
                            text = s.text,
                            type = s.type,
                            onClick = {
                                onEventSent(
                                    OrderListScreenContract.Event.SearchSuggestionClicked(
                                        suggestion = s.text,
                                        type = s.type
                                    )
                                )
                            },
                            onFilterClick = {
                                onEventSent(
                                    OrderListScreenContract.Event.ApplyFilters(
                                        chips = listOf(UiFilterChip(label = s.text, type = s.type))
                                    )
                                )
                            }
                        )
                    }
                }
            }

        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun OrderListTopBar(
    state: OrderListScreenContract.State,
    onEventSent: (event: OrderListScreenContract.Event) -> Unit,
) {
    TopAppBar(
        title = { Text("Collect Orders") },
        actions = {
            IconButton(onClick = { onEventSent(OrderListScreenContract.Event.Refresh) }) {
                Icon(Icons.Outlined.Refresh, contentDescription = "Refresh")
            }
        }
    )
}

@Composable
private fun SuggestionRow(
    text: String,
    type: OrderSearchSuggestionType,
    onClick: () -> Unit,
    onFilterClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        val leading = when (type) {
            OrderSearchSuggestionType.PHONE -> Icons.Outlined.Phone
            OrderSearchSuggestionType.ORDER_NUMBER -> Icons.Outlined.ReceiptLong
            OrderSearchSuggestionType.NAME -> Icons.Outlined.Public
        }
        Icon(leading, contentDescription = null)
        Spacer(Modifier.size(12.dp))
        Text(text, style = MaterialTheme.typography.bodyLarge, modifier = Modifier.weight(1f))
        androidx.compose.material3.AssistChip(
            onClick = onFilterClick,
            label = { Text("FILTER") },
            leadingIcon = { Icon(Icons.Outlined.Add, contentDescription = null) }
        )
    }
}

@Composable
private fun OrderCard(
    order: Order,
    modifier: Modifier = Modifier,
    showCheckbox: Boolean = false,
    checked: Boolean = false,
    onCheckedChange: ((Boolean) -> Unit)? = null,
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.Top
        ) {
            if (showCheckbox) {
                Checkbox(
                    checked = checked,
                    onCheckedChange = { onCheckedChange?.invoke(it) }
                )
                Spacer(Modifier.size(4.dp))
            }
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Header: customer type icon + customer/business name
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val icon = when (order.customerType) {
                        CustomerType.B2B -> Icons.Outlined.Business
                        CustomerType.B2C -> Icons.Outlined.Person
                    }
                    Icon(icon, contentDescription = null, tint = MaterialTheme.colorScheme.primary)
                    Spacer(Modifier.size(8.dp))
                    Text(
                        text = order.displayName,
                        style = MaterialTheme.typography.titleMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                // Second row: invoice number and web order number
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Outlined.ReceiptLong, contentDescription = null)
                    Spacer(Modifier.size(6.dp))
                    Text(text = order.invoiceNumber ?: "—", style = MaterialTheme.typography.bodyMedium)

                    Spacer(Modifier.size(16.dp))
                    Icon(Icons.Outlined.Public, contentDescription = null)
                    Spacer(Modifier.size(6.dp))
                    Text(
                        text = order.webOrderNumber ?: "—",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                // Time since picked chip
                TimeSincePickedChip(order.pickedAt)
            }
        }
    }
}

@Composable
private fun TimeSincePickedChip(pickedAt: Instant?) {
    val label = formatElapsedLabel(pickedAt)
    Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.tertiaryContainer)) {
        Row(
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(Icons.Outlined.BackHand, contentDescription = null)
            Spacer(Modifier.size(8.dp))
            Text(text = label, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

private fun formatElapsedLabel(pickedAt: Instant?): String {
    val ts = pickedAt ?: return "—"
    val now = Clock.System.now()
    val elapsed = now - ts
    val minutes = elapsed.inWholeMinutes
    return when {
        minutes <= 0 -> "—"
        minutes < 60 -> "$minutes minutes"
        minutes < 24 * 60 -> "${minutes / 60} hours"
        else -> "${minutes / (24 * 60)} days"
    }
}


@Composable
private fun FilterBar(
    state: OrderListScreenContract.State,
    onEventSent: (event: OrderListScreenContract.Event) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp, bottom = 8.dp)
            .horizontalScroll(rememberScrollState()),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Customer type toggles
        val b2bSelected = CustomerType.B2B in state.customerTypeFilterList
        val b2cSelected = CustomerType.B2C in state.customerTypeFilterList
        FilterChip(
            selected = b2bSelected,
            onClick = {
                onEventSent(
                    OrderListScreenContract.Event.ToggleCustomerType(
                        type = CustomerType.B2B,
                        checked = !b2bSelected
                    )
                )
            },
            label = { Text("B2B") },
            leadingIcon = { Icon(Icons.Outlined.Business, contentDescription = null) }
        )
        Spacer(Modifier.size(8.dp))
        FilterChip(
            selected = b2cSelected,
            onClick = {
                onEventSent(
                    OrderListScreenContract.Event.ToggleCustomerType(
                        type = CustomerType.B2C,
                        checked = !b2cSelected
                    )
                )
            },
            label = { Text("B2C") },
            leadingIcon = { Icon(Icons.Outlined.Person, contentDescription = null) }
        )

        // Applied filter chips
        state.appliedFilterChipList.forEach { chip ->
            Spacer(Modifier.size(8.dp))
            val icon = when (chip.type) {
                OrderSearchSuggestionType.PHONE -> Icons.Outlined.Phone
                OrderSearchSuggestionType.ORDER_NUMBER -> Icons.Outlined.ReceiptLong
                OrderSearchSuggestionType.NAME -> Icons.Outlined.Public
            }
            AssistChip(
                onClick = { onEventSent(OrderListScreenContract.Event.RemoveFilterChip(chip)) },
                label = { Text(chip.label) },
                leadingIcon = { Icon(icon, contentDescription = null) }
            )
        }

        if (state.appliedFilterChipList.isNotEmpty()) {
            Spacer(Modifier.size(8.dp))
            AssistChip(
                onClick = { onEventSent(OrderListScreenContract.Event.ResetFilters) },
                label = { Text("Clear") },
                leadingIcon = { Icon(Icons.Outlined.Close, contentDescription = null) }
            )
        }

        Spacer(Modifier.size(8.dp))
        if (!state.isMultiSelectionEnabled) {
            AssistChip(
                onClick = { onEventSent(OrderListScreenContract.Event.ToggleSelectionMode(true)) },
                label = { Text("SELECT") },
            )
        }
    }
}

@Composable
private fun SelectionBottomBar(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    selectedCount: Int,
    onCancel: () -> Unit,
    onConfirm: () -> Unit,
) {
    androidx.compose.material3.Surface(shadowElevation = 6.dp) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .windowInsetsPadding(
                    WindowInsets.navigationBars.only(WindowInsetsSides.Bottom)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            androidx.compose.material3.Checkbox(checked = checked, onCheckedChange = onCheckedChange)
            Spacer(Modifier.size(8.dp))
            Text("SELECT ALL", style = MaterialTheme.typography.bodyLarge)
            Spacer(modifier = Modifier.weight(1f))
            androidx.compose.material3.TextButton(onClick = onCancel) { Text("CANCEL") }
            Spacer(Modifier.size(8.dp))
            androidx.compose.material3.Button(onClick = onConfirm, enabled = selectedCount > 0) {
                Text("SELECT $selectedCount")
            }
        }
    }
}

@Preview(name = "Order list", showBackground = true, backgroundColor = 0xFFF5F5F5L, widthDp = 360, heightDp = 720)
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
