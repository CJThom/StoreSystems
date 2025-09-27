package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BackHand
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material.icons.outlined.Refresh
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Phone
import androidx.compose.material.icons.outlined.Add
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DockedSearchBar
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import com.gpcasiapac.storesystems.feature.collect.domain.model.Order
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.SearchSuggestionType
import com.gpcasiapac.storesystems.feature.collect.presentation.util.displayName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlin.time.Clock
import kotlin.time.Instant

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun OrderListScreen(
    state: OrderListScreenContract.State,
    onEventSent: (event: OrderListScreenContract.Event) -> Unit,
    effectFlow: Flow<OrderListScreenContract.Effect>,
    onOutcome: (outcome: OrderListScreenContract.Effect.Outcome) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(effectFlow) {
        effectFlow.collectLatest { effect ->
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
                        items(state.orderList) { order ->
                            OrderCard(
                                order = order,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clickable { onEventSent(OrderListScreenContract.Event.OpenOrder(order.id)) }
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
            val hasSuggestions = state.searchSuggestions.isNotEmpty()
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
                        onQueryChange = { onEventSent(OrderListScreenContract.Event.SearchTextChanged(it)) },
                        onSearch = { onEventSent(OrderListScreenContract.Event.SearchActiveChanged(false)) },
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
                    items(state.searchSuggestions.take(4)) { s ->
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
                                    OrderListScreenContract.Event.SearchSuggestionClicked(
                                        suggestion = s.text,
                                        type = s.type
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
    type: SearchSuggestionType,
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
            SearchSuggestionType.PHONE -> Icons.Outlined.Phone
            SearchSuggestionType.ORDER_NUMBER -> Icons.Outlined.ReceiptLong
            SearchSuggestionType.NAME -> Icons.Outlined.Public
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
) {
    Card(
        modifier = modifier,
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
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
                Text(text = order.webOrderNumber ?: "—", style = MaterialTheme.typography.bodyMedium)
            }

            // Time since picked chip
            TimeSincePickedChip(order.pickedAt)
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
