package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.BackHand
import androidx.compose.material.icons.outlined.Business
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.Public
import androidx.compose.material.icons.outlined.ReceiptLong
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.collect.domain.model.Order
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.presentation.util.displayName
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlin.time.Clock
import kotlin.time.Instant

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
        snackbarHost = {
            SnackbarHost(snackbarHostState)
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Top
        ) {
            Text("Collect Orders", style = MaterialTheme.typography.headlineSmall)

            if (state.isLoading) {
                CircularProgressIndicator()
            } else if (state.error != null) {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier
                    )
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp)) {
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
