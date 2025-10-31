package com.gpcasiapac.storesystems.feature.history.presentation.destination.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
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
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.history.presentation.composable.StatusBadge
import com.gpcasiapac.storesystems.feature.history.presentation.composable.formatTimeAgo
import com.gpcasiapac.storesystems.foundation.component.CheckboxCard
import com.gpcasiapac.storesystems.foundation.component.InvoiceSummary
import com.gpcasiapac.storesystems.foundation.component.ListItemScaffold
import com.gpcasiapac.storesystems.foundation.component.MBoltAppBar
import com.gpcasiapac.storesystems.foundation.component.TopBarTitle
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
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
                    snackbarHostState.showSnackbar(
                        effect.message,
                        duration = SnackbarDuration.Short
                    )

                is HistoryScreenContract.Effect.ShowError ->
                    snackbarHostState.showSnackbar(effect.error, duration = SnackbarDuration.Long)

                is HistoryScreenContract.Effect.Outcome -> onOutcome(effect)
            }
        }
    }

    Scaffold(
        topBar = {
            MBoltAppBar(
                title = {
                    TopBarTitle("History")
                },
                navigationIcon = {
                    IconButton(onClick = {
                        onEventSent(HistoryScreenContract.Event.Back)
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
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
                Card(
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = state.error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(12.dp)
                    )
                }
            }

            else -> {
                val items = state.items
                LazyColumn {
                    items(items, key = { it.id }) { historyItem ->
                        val invoiceNumbers = remember(historyItem) {
                            when (historyItem) {
                                is com.gpcasiapac.storesystems.feature.history.domain.model.CollectHistoryItem ->
                                    historyItem.metadata.map { meta -> meta.webOrderNumber ?: meta.invoiceNumber }
                                else -> emptyList()
                            }
                        }

                        val timeText = remember(historyItem) {
                            when (historyItem) {
                                is com.gpcasiapac.storesystems.feature.history.domain.model.CollectHistoryItem ->
                                    historyItem.metadata.firstOrNull()?.orderCreatedAt?.let { formatTimeAgo(it) } ?: ""
                                else -> ""
                            }
                        }

                        CheckboxCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(
                                    horizontal = Dimens.Space.medium,
                                    vertical = Dimens.Space.small
                                ).animateItem(),
                            isCheckable = false,
                            onCheckedChange = {},
                            onClick = {
                                onEventSent(HistoryScreenContract.Event.OpenItem(historyItem.id))
                            },
                            isChecked = false,
                        ) {
                            HistoryGroupedCard(
                                invoiceNumbers = invoiceNumbers,
                                time = timeText
                            )
                        }
                    }
                }
            }
        }
    }
}



@Composable
private fun HistoryGroupedCard(
    invoiceNumbers: List<String>,
    time: String,

) {
    ListItemScaffold(
        content = {
            Column {
                InvoiceSummary(invoiceNumbers)
            }
        },
        toolbar = {
            Row(
                modifier = Modifier.fillMaxWidth().padding(vertical = Dimens.Space.small),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row {
                    Text(
                        text = time,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(Dimens.Space.small))
                    StatusBadge(status = com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus.PENDING)
                }

                Button(onClick = {}) {
                    Text("Retry")
                }

            }
        }
    )
}
