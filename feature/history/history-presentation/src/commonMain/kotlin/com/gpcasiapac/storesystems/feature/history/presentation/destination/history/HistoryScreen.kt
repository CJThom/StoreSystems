package com.gpcasiapac.storesystems.feature.history.presentation.destination.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.lazy.itemsIndexed
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.material3.placeholder
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.gpcasiapac.storesystems.feature.history.api.HistoryType
import com.gpcasiapac.storesystems.feature.history.presentation.composable.StatusBadge
import com.gpcasiapac.storesystems.feature.history.presentation.composable.formatTimeAgo
import com.gpcasiapac.storesystems.foundation.component.OutlineCard
import com.gpcasiapac.storesystems.foundation.component.InvoiceSummarySection
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
        val itemsList = if (isLoading) List(6) { null } else state.items

        LazyColumn {
            itemsIndexed(itemsList, key = { index, item -> item?.id ?: "skeleton-$index" }) { _, historyItem ->
                val invoiceNumbers = if (!isLoading && historyItem != null) {
                    when (historyItem) {
                        is com.gpcasiapac.storesystems.feature.history.domain.model.CollectHistoryItem ->
                            historyItem.metadata.map { meta -> meta.webOrderNumber ?: meta.invoiceNumber }
                        else -> emptyList()
                    }
                } else emptyList()

                val timeText = if (!isLoading && historyItem != null) {
                    when (historyItem) {
                        is com.gpcasiapac.storesystems.feature.history.domain.model.CollectHistoryItem ->
                            historyItem.metadata.firstOrNull()?.orderCreatedAt?.let { formatTimeAgo(it) } ?: ""
                        else -> ""
                    }
                } else ""

                val customerName = if (!isLoading && historyItem != null) {
                    when (historyItem) {
                        is com.gpcasiapac.storesystems.feature.history.domain.model.CollectHistoryItem -> historyItem.metadata.firstOrNull()?.getCustomerDisplayName() ?: ""
                        else -> ""
                    }
                } else ""

                val status = if (!isLoading && historyItem != null) {
                    when (historyItem) {
                        is com.gpcasiapac.storesystems.feature.history.domain.model.CollectHistoryItem -> historyItem.status
                        else -> com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus.PENDING
                    }
                } else com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus.PENDING

                OutlineCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(
                            horizontal = Dimens.Space.medium,
                            vertical = Dimens.Space.small
                        )
                        .animateItem(),
                    onClick = {
                        if (!isLoading && historyItem != null) {
                            val type = when (historyItem) {
                                is com.gpcasiapac.storesystems.feature.history.domain.model.CollectHistoryItem -> HistoryType.ORDER_SUBMISSION
                                else -> HistoryType.UNKNOWN
                            }
                            onEventSent(HistoryScreenContract.Event.OpenItem(type = type, id = historyItem.id))
                        }
                    }
                ) {
                    HistoryGroupedCard(
                        invoiceNumbers = invoiceNumbers,
                        time = timeText,
                        customerName = customerName,
                        isLoading = isLoading,
                        status = status,
                        onRetry = if (!isLoading && (status == com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus.FAILED || status == com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus.REQUIRES_ACTION) && historyItem != null) {
                            { onEventSent(HistoryScreenContract.Event.RetryItem(historyItem.id)) }
                        } else null
                    )
                }
            }
        }
    }
}



@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
private fun HistoryGroupedCard(
    customerName: String,
    invoiceNumbers: List<String>,
    time: String,
    isLoading: Boolean = false,
    status: com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus = com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus.PENDING,
    onRetry: (() -> Unit)? = null,
) {
    ListItemScaffold(
        content = {
            Column {
                if (isLoading) {
                    // Skeleton blocks approximating text content
                    Box(
                        Modifier
                            .fillMaxWidth(0.6f)
                            .height(16.dp)
                            .placeholder(true, shape = RoundedCornerShape(4.dp))
                    )
                    Spacer(Modifier.height(Dimens.Space.small))
                    Box(
                        Modifier
                            .fillMaxWidth(0.8f)
                            .height(14.dp)
                            .placeholder(true, shape = RoundedCornerShape(4.dp))
                    )
                    Spacer(Modifier.height(Dimens.Space.extraSmall))
                    Box(
                        Modifier
                            .fillMaxWidth(0.5f)
                            .height(14.dp)
                            .placeholder(true, shape = RoundedCornerShape(4.dp))
                    )
                } else {
                    InvoiceSummarySection(
                        invoices = invoiceNumbers,
                        customerName = customerName
                    )
                }
            }
        },
        toolbar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = Dimens.Space.small),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isLoading) " " else time,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.placeholder(isLoading)
                    )
                    Spacer(modifier = Modifier.width(Dimens.Space.small))
                    if (isLoading) {
                        Box(
                            Modifier
                                .width(72.dp)
                                .height(20.dp)
                                .placeholder(true, shape = RoundedCornerShape(50))
                        )
                    } else {
                        StatusBadge(status = status)
                    }
                }
                if (isLoading) {
                    Box(
                        Modifier
                            .width(64.dp)
                            .height(ButtonDefaults.ExtraSmallContainerHeight)
                            .placeholder(true, shape = RoundedCornerShape(8.dp))
                    )
                } else {
                    when (status) {
                        com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus.IN_PROGRESS -> {
                            CircularProgressIndicator(
                                strokeWidth = 2.dp,
                                modifier = Modifier.height(ButtonDefaults.ExtraSmallContainerHeight)
                            )
                        }
                        com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus.FAILED,
                        com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus.REQUIRES_ACTION -> {
                            if (onRetry != null) {
                                Button(
                                    onClick = onRetry,
                                    contentPadding = ButtonDefaults.contentPaddingFor(ButtonDefaults.ExtraSmallContainerHeight),
                                    modifier = Modifier.height(ButtonDefaults.ExtraSmallContainerHeight),
                                ) {
                                    Text(
                                        "Retry",
                                        style = ButtonDefaults.textStyleFor(ButtonDefaults.ExtraSmallContainerHeight)
                                    )
                                }
                            }
                        }
                        else -> {
                            // No action for other states
                        }
                    }
                }
            }
        }
    )
}
