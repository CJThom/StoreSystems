package com.gpcasiapac.storesystems.feature.history.presentation.destination.history

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.history.presentation.model.HistoryStatusColor
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
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

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("History") },
                modifier = Modifier.fillMaxWidth(),
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Content(padding, state, onEventSent)
    }
}

@Composable
private fun Content(
    padding: PaddingValues,
    state: HistoryScreenContract.State,
    onEventSent: (event: HistoryScreenContract.Event) -> Unit = {},
) {
    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
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
                        ListItem(
                            headlineContent = {
                                Text(
                                    text = item.title,
                                    style = MaterialTheme.typography.titleMedium
                                )
                            },
                            supportingContent = {
                                Column {
                                    Text(
                                        text = item.subtitle,
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    if (item.attempts != null) {
                                        Text(
                                            text = item.attempts,
                                            style = MaterialTheme.typography.bodySmall,
                                            color = MaterialTheme.colorScheme.error
                                        )
                                    }
                                }
                            },
                            trailingContent = {
                                Surface(
                                    shape = MaterialTheme.shapes.small,
                                    color = when (item.statusColor) {
                                        HistoryStatusColor.PENDING -> MaterialTheme.colorScheme.secondaryContainer
                                        HistoryStatusColor.SUCCESS -> MaterialTheme.colorScheme.primaryContainer
                                        HistoryStatusColor.ERROR -> MaterialTheme.colorScheme.errorContainer
                                        HistoryStatusColor.INFO -> MaterialTheme.colorScheme.tertiaryContainer
                                    }
                                ) {
                                    Text(
                                        text = item.statusText,
                                        style = MaterialTheme.typography.labelMedium,
                                        color = when (item.statusColor) {
                                            HistoryStatusColor.PENDING -> MaterialTheme.colorScheme.onSecondaryContainer
                                            HistoryStatusColor.SUCCESS -> MaterialTheme.colorScheme.onPrimaryContainer
                                            HistoryStatusColor.ERROR -> MaterialTheme.colorScheme.onErrorContainer
                                            HistoryStatusColor.INFO -> MaterialTheme.colorScheme.onTertiaryContainer
                                        },
                                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                                    )
                                }
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 8.dp)
                                .clickable { onEventSent(HistoryScreenContract.Event.OpenItem(item.id)) }
                        )
                    }
                }
            }
        }
    }
}
