package com.gpcasiapac.storesystems.feature.history.presentation.destination.history

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
fun HistoryScreen(
    state: HistoryScreenContract.State,
    onEventSent: (event: HistoryScreenContract.Event) -> Unit,
    effectFlow: Flow<HistoryScreenContract.Effect>,
    onOutcome: (outcome: HistoryScreenContract.Effect.Outcome) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(effectFlow) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is HistoryScreenContract.Effect.ShowToast ->
                    snackbarHostState.showSnackbar(effect.message, duration = SnackbarDuration.Short)
                is HistoryScreenContract.Effect.ShowError ->
                    snackbarHostState.showSnackbar(effect.error, duration = SnackbarDuration.Long)
                is HistoryScreenContract.Effect.Outcome -> onOutcome(effect)
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Content(padding, state)
    }
}

@Composable
private fun Content(
    padding: PaddingValues,
    state: HistoryScreenContract.State,
) {
    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text("History", style = MaterialTheme.typography.headlineSmall)

        when {
            state.isLoading -> {
                CircularProgressIndicator()
                Text("Loading history…", modifier = Modifier.padding(top = 8.dp))
            }
            state.error != null -> {
                Card(colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }
            else -> {
                LazyColumn(contentPadding = PaddingValues(16.dp)) {
                    items(state.items) { item ->
                        Card(
                            modifier = Modifier
                                .padding(bottom = 8.dp),
                            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                        ) {
                            Text(item, modifier = Modifier.padding(12.dp))
                        }
                    }
                }
            }
        }
    }
}
