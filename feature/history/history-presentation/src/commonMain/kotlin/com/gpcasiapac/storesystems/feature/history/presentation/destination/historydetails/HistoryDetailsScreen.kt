package com.gpcasiapac.storesystems.feature.history.presentation.destination.historydetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.Crossfade
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.DateRange
import androidx.compose.material.icons.outlined.PersonOutline
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.common.kotlin.extension.toLocalDateTimeString
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.material3.placeholder
import com.gpcasiapac.storesystems.feature.history.api.HistoryType
import com.gpcasiapac.storesystems.feature.history.domain.model.CollectHistoryItem
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryMetadata
import com.gpcasiapac.storesystems.feature.history.domain.model.HistoryStatus
import com.gpcasiapac.storesystems.feature.history.presentation.composable.CollectMetadataRowCard
import com.gpcasiapac.storesystems.feature.history.presentation.composable.CollectMetadataRowCardSkeleton
import com.gpcasiapac.storesystems.feature.history.presentation.composable.ErrorInfo
import com.gpcasiapac.storesystems.feature.history.presentation.composable.HistoryStatusIcon
import com.gpcasiapac.storesystems.feature.history.presentation.composable.HistoryStatusText
import com.gpcasiapac.storesystems.foundation.component.MBoltAppBar
import com.gpcasiapac.storesystems.foundation.component.TopBarTitle
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.emptyFlow
import kotlin.time.Clock

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
        val isLoading = state.isLoading
        val item = state.item

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
        ) {
            // Status Icon and Text Section
            if (item != null) {
                item {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .animateContentSize()
                            .padding(vertical = Dimens.Space.large),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Crossfade(item.status, label = "HistoryStatusIcon") {
                            when (it) {
                                HistoryStatus.RETRYING, HistoryStatus.IN_PROGRESS -> CircularProgressIndicator(
                                    modifier = Modifier.placeholder(isLoading)
                                )

                                else -> HistoryStatusIcon(
                                    status = item.status,
                                    modifier = Modifier.size(Dimens.Size.iconExtraLarge)
                                        .placeholder(isLoading),
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(Dimens.Space.small))
                        HistoryStatusText(
                            status = item.status,
                            modifier = Modifier.placeholder(isLoading)
                        )

                        AnimatedVisibility(
                            (state.item.status == HistoryStatus.FAILED || state.item.status == HistoryStatus.REQUIRES_ACTION),
                            enter = fadeIn() + scaleIn(),
                            exit = fadeOut()
                        ) {

                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                ErrorInfo(
                                    modifier = Modifier.padding(Dimens.Space.medium)
                                        .placeholder(isLoading),
                                    message = state.item.lastError ?: "Unknown error",
                                    attempts = state.item.attempts,
                                )
                                Button(
                                    modifier = Modifier.placeholder(isLoading),
                                    onClick = {
                                        onEvent(HistoryDetailsScreenContract.Event.Retry)
                                    }) {
                                    Row(
                                        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.small),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Icon(Icons.Default.Refresh, "Retry Request")
                                        Text("Retry")
                                    }
                                }
                                Spacer(modifier = Modifier.height(Dimens.Space.medium))
                            }
                        }

                    }
                }
            }
            item {
                HorizontalDivider()
            }

            // Submission Info Row
            if (item != null) {
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = Dimens.Space.medium),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.placeholder(isLoading)
                        ) {
                            Icon(
                                Icons.Outlined.DateRange,
                                "SubmittedBy",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(Dimens.Space.small))
                            SubmissionInfoItem(
                                label = "Submitted On",
                                value = item.timestamp.toLocalDateTimeString()
                            )
                        }
                        // Submitted On


                        // Submitted By
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.placeholder(isLoading)
                        ) {
                            Icon(
                                Icons.Outlined.PersonOutline,
                                "SubmittedBy",
                                tint = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Spacer(modifier = Modifier.width(Dimens.Space.small))
                            SubmissionInfoItem(
                                label = "Submitted By",
                                value = "Abc"
                            )
                        }
                    }
                }
            }

            item {
                HorizontalDivider()
            }
            item {
                Spacer(modifier = Modifier.height(Dimens.Space.medium))
            }

            item {
                Column {
                    Column(
                        modifier = Modifier.padding(Dimens.Space.medium),
                        verticalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall)
                    ) {

                        Text(
                            text = "Orders", // TODO: Use String resource
                            style = MaterialTheme.typography.headlineSmall,
                            modifier = Modifier.placeholder(isLoading)
                        )

                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(Dimens.Space.extraSmall),
                            modifier = Modifier.placeholder(isLoading)
                        ) {
                            Text(
                                text = when (item) {
                                    is CollectHistoryItem -> item.metadata.size
                                    else -> 0
                                }.toString(),
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                            Text(
                                text = "Orders submitted",
                                style = MaterialTheme.typography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }
                }
            }

            // Order List Section
            if (isLoading) {
                items(5) { _ ->
                    CollectMetadataRowCardSkeleton(
                        modifier = Modifier
                            .padding(Dimens.Space.medium)
                            .fillMaxWidth()

                    )
                }
            } else {
                when (val resolved = item) {
                    is CollectHistoryItem -> {
                        items(resolved.metadata, key = { it.invoiceNumber }) { line ->
                            Column {
                                CollectMetadataRowCard(
                                    metadata = line,
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(Dimens.Space.medium)

                                )
                                HorizontalDivider()
                            }
                        }
                    }

                    else -> { /* optional empty state */
                    }
                }
            }
        }
    }
}

@Composable
private fun SubmissionInfoItem(
    label: String,
    value: String,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier,
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.SemiBold,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Preview
@Composable
fun HistoryDetailsScreenLoadingPreview() {
    GPCTheme {
        HistoryDetailsScreen(
            type = HistoryType.ORDER_SUBMISSION,
            id = "",
            onEvent = {},
            state = HistoryDetailsScreenContract.State(
                isLoading = true,
                item = CollectHistoryItem(
                    id = "",
                    entityId = "",
                    status = HistoryStatus.REQUIRES_ACTION,
                    timestamp = Clock.System.now(),
                    metadata = listOf(
                        HistoryMetadata.CollectMetadata(
                            invoiceNumber = "INV-123",
                            orderNumber = "ORD-123",
                            webOrderNumber = "WEB-123",
                            createdDateTime = Clock.System.now(),
                            invoiceDateTime = Clock.System.now(),
                            customerType = "B2B",
                            phone = "123456789",
                            customerNumber = "123456789",
                            name = "John Doe"
                        ),
                        HistoryMetadata.CollectMetadata(
                            invoiceNumber = "INV-1234",
                            orderNumber = "ORD-123",
                            webOrderNumber = "WEB-123",
                            createdDateTime = Clock.System.now(),
                            invoiceDateTime = Clock.System.now(),
                            customerType = "B2B",
                            phone = "123456789",
                            customerNumber = "123456789",
                            name = "John Doe"
                        ),
                        HistoryMetadata.CollectMetadata(
                            invoiceNumber = "INV-12356",
                            orderNumber = "ORD-123",
                            webOrderNumber = "WEB-123",
                            createdDateTime = Clock.System.now(),
                            invoiceDateTime = Clock.System.now(),
                            customerType = "B2B",
                            phone = "123456789",
                            customerNumber = "123456789",
                            name = "John Doe"
                        )
                    ),
                    attempts = 0,
                    lastError = "There has been an error submitting your request, please retry sometime later.",
                    submittedBy = "Callum Thomson",
                    priority = 0,
                    requestId = "REQ-123"
                )
            ),
            onOutcome = {},
            effectFlow = emptyFlow()
        )
    }
}


@Preview
@Composable
fun HistoryDetailsScreenErrorPreview() {
    GPCTheme {
        HistoryDetailsScreen(
            type = HistoryType.ORDER_SUBMISSION,
            id = "",
            onEvent = {},
            state = HistoryDetailsScreenContract.State(
                isLoading = false,
                item = CollectHistoryItem(
                    id = "",
                    entityId = "",
                    status = HistoryStatus.REQUIRES_ACTION,
                    timestamp = Clock.System.now(),
                    metadata = listOf(
                        HistoryMetadata.CollectMetadata(
                            invoiceNumber = "INV-123",
                            orderNumber = "ORD-123",
                            webOrderNumber = "WEB-123",
                            createdDateTime = Clock.System.now(),
                            invoiceDateTime = Clock.System.now(),
                            customerType = "B2B",
                            phone = "123456789",
                            customerNumber = "123456789",
                            name = "John Doe"
                        ),
                        HistoryMetadata.CollectMetadata(
                            invoiceNumber = "INV-1234",
                            orderNumber = "ORD-123",
                            webOrderNumber = "WEB-123",
                            createdDateTime = Clock.System.now(),
                            invoiceDateTime = Clock.System.now(),
                            customerType = "B2B",
                            phone = "123456789",
                            customerNumber = "123456789",
                            name = "John Doe"
                        ),
                        HistoryMetadata.CollectMetadata(
                            invoiceNumber = "INV-12356",
                            orderNumber = "ORD-123",
                            webOrderNumber = "WEB-123",
                            createdDateTime = Clock.System.now(),
                            invoiceDateTime = Clock.System.now(),
                            customerType = "B2B",
                            phone = "123456789",
                            customerNumber = "123456789",
                            name = "John Doe"
                        )
                    ),
                    attempts = 0,
                    lastError = "There has been an error submitting your request, please retry sometime later.",
                    submittedBy = "Callum Thomson",
                    priority = 0,
                    requestId = "REQ-123"
                )
            ),
            onOutcome = {},
            effectFlow = emptyFlow()
        )
    }
}

@Preview
@Composable
fun HistoryDetailsScreenPreview() {
    GPCTheme {
        HistoryDetailsScreen(
            type = HistoryType.ORDER_SUBMISSION,
            id = "",
            onEvent = {},
            state = HistoryDetailsScreenContract.State(
                isLoading = false,
                item = CollectHistoryItem(
                    id = "",
                    entityId = "",
                    status = HistoryStatus.COMPLETED,
                    timestamp = Clock.System.now(),
                    metadata = listOf(
                        HistoryMetadata.CollectMetadata(
                            invoiceNumber = "INV-123",
                            orderNumber = "ORD-123",
                            webOrderNumber = "WEB-123",
                            createdDateTime = Clock.System.now(),
                            invoiceDateTime = Clock.System.now(),
                            customerType = "B2B",
                            phone = "123456789",
                            customerNumber = "123456789",
                            name = "John Doe"
                        ),
                        HistoryMetadata.CollectMetadata(
                            invoiceNumber = "INV-12311414",
                            orderNumber = "ORD-123",
                            webOrderNumber = "WEB-123",
                            createdDateTime = Clock.System.now(),
                            invoiceDateTime = Clock.System.now(),
                            customerType = "B2B",
                            phone = "123456789",
                            customerNumber = "123456789",
                            name = "John Doe"
                        ),
                        HistoryMetadata.CollectMetadata(
                            invoiceNumber = "INV-123114",
                            orderNumber = "ORD-123",
                            webOrderNumber = "WEB-123",
                            createdDateTime = Clock.System.now(),
                            invoiceDateTime = Clock.System.now(),
                            customerType = "B2B",
                            phone = "123456789",
                            customerNumber = "123456789",
                            name = "John Doe"
                        )
                    ),
                    attempts = 0,
                    lastError = null,
                    submittedBy = "Callum Thomson",
                    priority = 0,
                    requestId = "REQ-123"
                )
            ),
            onOutcome = {},
            effectFlow = emptyFlow()
        )
    }
}