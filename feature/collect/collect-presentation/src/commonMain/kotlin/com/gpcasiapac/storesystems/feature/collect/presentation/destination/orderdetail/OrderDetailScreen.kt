package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
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
import com.gpcasiapac.storesystems.feature.collect.presentation.util.displayName

@Composable
fun OrderDetailScreen(
    state: OrderDetailScreenContract.State,
    onEventSent: (event: OrderDetailScreenContract.Event) -> Unit,
    effectFlow: Flow<OrderDetailScreenContract.Effect>,
    onOutcome: (outcome: OrderDetailScreenContract.Effect.Outcome) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(effectFlow) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is OrderDetailScreenContract.Effect.ShowToast ->
                    snackbarHostState.showSnackbar(effect.message, duration = SnackbarDuration.Short)
                is OrderDetailScreenContract.Effect.ShowError ->
                    snackbarHostState.showSnackbar(effect.error, duration = SnackbarDuration.Long)
                is OrderDetailScreenContract.Effect.Outcome -> onOutcome(effect)
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Content(padding, state, onEventSent)
    }
}

@Composable
private fun Content(
    padding: PaddingValues,
    state: OrderDetailScreenContract.State,
    onEventSent: (event: OrderDetailScreenContract.Event) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        when {
            state.isLoading -> {
                CircularProgressIndicator()
                Text("Loading order…", modifier = Modifier.padding(top = 8.dp))
            }

            state.error != null -> {
                Text(
                    text = state.error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
                Button(onClick = { onEventSent(OrderDetailScreenContract.Event.Refresh) }, modifier = Modifier.padding(top = 12.dp)) {
                    Text("Retry")
                }
            }

            else -> {
                val title = state.order?.displayName ?: "Order ${'$'}{state.orderId}"
                Text(title, style = MaterialTheme.typography.headlineSmall)
                Button(onClick = { onEventSent(OrderDetailScreenContract.Event.Back) }, modifier = Modifier.padding(top = 16.dp)) {
                    Text("Back")
                }
            }
        }
    }
}
