package com.gpcasiapac.storesystems.feature.history.presentation.destination.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
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
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus
import com.gpcasiapac.storesystems.feature.history.presentation.composable.HistoryGroupedCard
import com.gpcasiapac.storesystems.feature.history.presentation.composable.formatTimeAgo
import com.gpcasiapac.storesystems.feature.history.presentation.model.HistoryListItemState
import com.gpcasiapac.storesystems.foundation.component.MBoltAppBar
import com.gpcasiapac.storesystems.foundation.component.OutlineCard
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
        // Inline error banner without forking the whole UI
        state.error?.let { error ->
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                Text(
                    text = error,
                    color = MaterialTheme.colorScheme.onErrorContainer,
                    modifier = Modifier.padding(12.dp)
                )
            }
        }

        val isLoading = state.isLoading
        val itemsList: List<HistoryListItemState?> = if (isLoading) List(6) { null } else state.uiItems

        LazyColumn {
            itemsIndexed(itemsList, key = { index, item -> item?.id ?: "skeleton-$index" }) { _, item ->
                val invoiceNumbers = if (!isLoading && item != null) item.invoiceNumbers else emptyList()
                val timeText = if (!isLoading && item != null) item.submittedAt?.let { formatTimeAgo(it) } ?: "" else ""
                val customerName = if (!isLoading && item != null) item.customerName else ""
                val status = if (!isLoading && item != null) item.status else HistoryStatus.PENDING

                OutlineCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = Dimens.Space.medium,
                            vertical = Dimens.Space.small
                        )
                        .animateItem(),
                    onClick = {
                        if (!isLoading && item != null) {
                            onEventSent(HistoryScreenContract.Event.OpenItem(type = item.type, id = item.id))
                        }
                    }
                ) {
                    HistoryGroupedCard(
                        invoiceNumbers = invoiceNumbers,
                        time = timeText,
                        customerName = customerName,
                        isLoading = isLoading,
                        status = status,
                        onRetry = if (!isLoading && item?.canRetry == true) {
                            { onEventSent(HistoryScreenContract.Event.RetryItem(item.id)) }
                        } else null
                    )
                }
            }
        }
    }
}



