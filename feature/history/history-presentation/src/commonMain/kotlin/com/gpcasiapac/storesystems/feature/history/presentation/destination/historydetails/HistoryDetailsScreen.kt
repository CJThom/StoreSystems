package com.gpcasiapac.storesystems.feature.history.presentation.destination.historydetails

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.material3.placeholder
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.OutlinedButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.history.api.HistoryType
import com.gpcasiapac.storesystems.feature.history.presentation.composable.HistoryItemCard
import com.gpcasiapac.storesystems.feature.history.presentation.mapper.toCustomerTypeParam
import com.gpcasiapac.storesystems.feature.history.presentation.destination.history.HistoryScreenContract
import com.gpcasiapac.storesystems.foundation.component.CheckboxCard
import com.gpcasiapac.storesystems.foundation.component.HeaderMedium
import com.gpcasiapac.storesystems.foundation.component.MBoltAppBar
import com.gpcasiapac.storesystems.foundation.component.OutlineCard
import com.gpcasiapac.storesystems.foundation.component.TopBarTitle
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailsScreen(
    type: HistoryType,
    id: String,
    state: HistoryDetailsScreenContract.State,
    onEvent: (HistoryDetailsScreenContract.Event) -> Unit,
    effectFlow: Flow<HistoryDetailsScreenContract.Effect>,
    onOutcome: (HistoryDetailsScreenContract.Effect.Outcome) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(type, id) {
        onEvent(
            HistoryDetailsScreenContract.Event.Initialize(type = type, id = id)
        )
    }


    LaunchedEffect(effectFlow) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is HistoryDetailsScreenContract.Effect.ShowError -> snackbarHostState.showSnackbar(
                    effect.message
                )

                is HistoryDetailsScreenContract.Effect.Outcome -> onOutcome(effect)
            }
        }
    }

    Scaffold(
        topBar = {
            MBoltAppBar(
                title = { TopBarTitle("History Details") },
                navigationIcon = {
                    IconButton(onClick = { onEvent(HistoryDetailsScreenContract.Event.Back) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding -> Content(padding, state, onEvent) }
}

@Composable
private fun Content(
    padding: PaddingValues,
    state: HistoryDetailsScreenContract.State,
    onEvent: (HistoryDetailsScreenContract.Event) -> Unit
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
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = Dimens.Space.medium, vertical = Dimens.Space.small),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.errorContainer
                )
            ) {
                Column(modifier = Modifier.padding(Dimens.Space.medium)) {
                    Text(
                        text = error,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onErrorContainer
                    )
                }
            }
        }

        val isLoading = state.isLoading
        val item = state.item

        LazyColumn {
            // Summary header
            item {
                HeaderMedium(
                    text = "Summary",
                    isLoading = isLoading,
                    contentPadding = PaddingValues(
                        horizontal = Dimens.Space.medium,
                        vertical = Dimens.Space.small
                    )
                )
            }

            // Summary information panel (skeleton while loading)
            item {
                InfoPanel(
                    modifier = Modifier.padding(horizontal = Dimens.Space.medium, vertical = Dimens.Space.small),
                    contentPadding = PaddingValues(Dimens.Space.medium)
                ) {
                    if (isLoading) {
                        // Two lines approximating: Submitted on / Submitted by
                        Box(
                            Modifier
                                .fillMaxWidth(0.5f)
                                .height(16.dp)
                                .placeholder(true, shape = RoundedCornerShape(4.dp))
                        )
                        Spacer(Modifier.height(Dimens.Space.small))
                        Box(
                            Modifier
                                .fillMaxWidth(0.6f)
                                .height(16.dp)
                                .placeholder(true, shape = RoundedCornerShape(4.dp))
                        )
                    } else {
                        when (val resolved = item) {
                            is com.gpcasiapac.storesystems.feature.history.domain.model.CollectHistoryItem -> {
                                SummarySection(resolved)
                            }
                            else -> { /* no-op */ }
                        }
                    }
                }
            }

            // Order List header
            item {
                HeaderMedium(
                    text = "Order List",
                    isLoading = isLoading,
                    contentPadding = PaddingValues(
                        horizontal = Dimens.Space.medium,
                        vertical = Dimens.Space.small
                    )
                )
            }

            if (isLoading) {
                items(5) { _ ->
                    CollectMetadataRowCardSkeleton(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(horizontal = 16.dp, vertical = 8.dp)
                    )
                }
            } else {
                when (val resolved = item) {
                    is com.gpcasiapac.storesystems.feature.history.domain.model.CollectHistoryItem -> {
                        items(resolved.metadata, key = { it.invoiceNumber }) { line ->
                            CollectMetadataRowCard(
                                metadata = line,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                    else -> { /* optional empty state */ }
                }
            }
        }
    }
}


@androidx.compose.runtime.Composable
private fun CollectMetadataRowCard(
    metadata: com.gpcasiapac.storesystems.feature.history.domain.model.HistoryMetadata.CollectMetadata,
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    com.gpcasiapac.storesystems.foundation.component.ListItemScaffold(
        modifier = modifier,
        contentPadding = androidx.compose.foundation.layout.PaddingValues()
    ) {
        com.gpcasiapac.storesystems.foundation.component.CollectOrderDetailsContent(
            customerName = metadata.getCustomerDisplayName(),
            customerType = metadata.customerType.toCustomerTypeParam(),
            invoiceNumber = metadata.invoiceNumber,
            webOrderNumber = metadata.webOrderNumber,
            isLoading = false,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                bottom = com.gpcasiapac.storesystems.foundation.design_system.Dimens.Space.small
            )
        )
    }
}


@androidx.compose.runtime.Composable
private fun CollectMetadataRowCardSkeleton(
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier
) {
    com.gpcasiapac.storesystems.foundation.component.ListItemScaffold(
        modifier = modifier,
        contentPadding = androidx.compose.foundation.layout.PaddingValues()
    ) {
        com.gpcasiapac.storesystems.foundation.component.CollectOrderDetailsContent(
            customerName = "",
            customerType = com.gpcasiapac.storesystems.foundation.component.CustomerTypeParam.B2C,
            invoiceNumber = "",
            webOrderNumber = null,
            isLoading = true,
            contentPadding = androidx.compose.foundation.layout.PaddingValues(
                bottom = com.gpcasiapac.storesystems.foundation.design_system.Dimens.Space.small
            )
        )
    }
}


@Composable
private fun SummarySection(
    resolved: com.gpcasiapac.storesystems.feature.history.domain.model.CollectHistoryItem
) {
    val meta = resolved.metadata.firstOrNull()
    val submittedAtText = meta?.orderCreatedAt
        ?.let { com.gpcasiapac.storesystems.feature.history.presentation.composable.formatTimeAgo(it) }
        ?: "-"
    val submittedByText = meta?.getCustomerDisplayName() ?: "-"
    val itemCount = resolved.metadata.size

    // Title row with status
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
        Text(
            text = "Submission",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onSurface
        )
        com.gpcasiapac.storesystems.feature.history.presentation.composable.StatusBadge(
            status = resolved.status
        )
    }

    Spacer(Modifier.height(com.gpcasiapac.storesystems.foundation.design_system.Dimens.Space.small))

    // Submitted on/by
    KeyValueRow(label = "Submitted on", value = submittedAtText)
    KeyValueRow(label = "Submitted by", value = submittedByText)

    // Items count
    KeyValueRow(label = "Items", value = itemCount.toString())

    // Attempts
    KeyValueRow(label = "Attempts", value = resolved.attempts.toString())

    // Last error (only if present)
    if (!resolved.lastError.isNullOrBlank()) {
        Spacer(Modifier.height(com.gpcasiapac.storesystems.foundation.design_system.Dimens.Space.small))
        ErrorInfo(message = resolved.lastError!!)
    }
}

@Composable
private fun KeyValueRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onSurfaceVariant,
    singleLine: Boolean = false,
    maxValueLines: Int = 3,
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(com.gpcasiapac.storesystems.foundation.design_system.Dimens.Space.small)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            modifier = Modifier.weight(1f),
            maxLines = 1,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            color = valueColor,
            modifier = Modifier.weight(1.5f),
            maxLines = if (singleLine) 1 else maxValueLines,
            overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis,
            softWrap = true,
            textAlign = androidx.compose.ui.text.style.TextAlign.End
        )
    }
}

@Composable
private fun ErrorInfo(message: String) {
    androidx.compose.material3.Surface(
        color = MaterialTheme.colorScheme.errorContainer,
        contentColor = MaterialTheme.colorScheme.onErrorContainer,
        shape = MaterialTheme.shapes.small
    ) {
        Column(modifier = Modifier.padding(com.gpcasiapac.storesystems.foundation.design_system.Dimens.Space.small)) {
            Text(
                text = message,
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 5,
                overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
            )
        }
    }
}

@androidx.compose.runtime.Composable
private fun InfoPanel(
    modifier: androidx.compose.ui.Modifier = androidx.compose.ui.Modifier,
    contentPadding: androidx.compose.foundation.layout.PaddingValues = androidx.compose.foundation.layout.PaddingValues(16.dp),
    showBorder: Boolean = false,
    content: @androidx.compose.runtime.Composable () -> Unit,
) {
    val border = if (showBorder) {
        androidx.compose.foundation.BorderStroke(1.dp, androidx.compose.material3.MaterialTheme.colorScheme.outlineVariant)
    } else null

    androidx.compose.material3.Surface(
        modifier = modifier,
        color = androidx.compose.material3.MaterialTheme.colorScheme.surfaceContainer,
        tonalElevation = 1.dp,
        shadowElevation = 0.dp,
        shape = androidx.compose.material3.MaterialTheme.shapes.medium,
        border = border
    ) {
        androidx.compose.foundation.layout.Column(
            androidx.compose.ui.Modifier.padding(contentPadding)
        ) { content() }
    }
}
