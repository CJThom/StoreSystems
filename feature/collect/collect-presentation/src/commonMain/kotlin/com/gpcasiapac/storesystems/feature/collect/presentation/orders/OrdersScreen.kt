package com.gpcasiapac.storesystems.feature.collect.presentation.orders

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@Composable
fun OrdersScreen(
    state: OrdersScreenContract.State,
    onEventSent: (event: OrdersScreenContract.Event) -> Unit,
    effectFlow: Flow<OrdersScreenContract.Effect>,
    onOutcome: (outcome: OrdersScreenContract.Effect.Outcome) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

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
                    items(state.orders) { order ->
                        Card(
                            modifier = Modifier
                                .fillMaxSize()
                                .clickable { onEventSent(OrdersScreenContract.Event.OpenOrder(order.id)) },
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text(order.title, modifier = Modifier)
                        }
                    }
                }
            }
        }
    }
}
