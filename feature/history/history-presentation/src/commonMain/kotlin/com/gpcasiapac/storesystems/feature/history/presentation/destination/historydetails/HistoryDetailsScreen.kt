package com.gpcasiapac.storesystems.feature.history.presentation.destination.historydetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.common.presentation.compose.placeholder.material3.placeholder
import com.gpcasiapac.storesystems.feature.history.api.HistoryType
import com.gpcasiapac.storesystems.feature.history.domain.model.CollectHistoryItem
import com.gpcasiapac.storesystems.feature.history.presentation.composable.CollectMetadataRowCard
import com.gpcasiapac.storesystems.feature.history.presentation.composable.CollectMetadataRowCardSkeleton
import com.gpcasiapac.storesystems.feature.history.presentation.composable.ErrorInfo
import com.gpcasiapac.storesystems.feature.history.presentation.composable.InfoPanel
import com.gpcasiapac.storesystems.feature.history.presentation.composable.SummarySection
import com.gpcasiapac.storesystems.foundation.component.HeaderMedium
import com.gpcasiapac.storesystems.foundation.component.MBoltAppBar
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
                    modifier = Modifier.padding(
                        horizontal = Dimens.Space.medium,
                        vertical = Dimens.Space.small
                    ),
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
                            is CollectHistoryItem -> {
                                SummarySection(resolved)


                            }

                            else -> { /* no-op */
                            }
                        }
                    }
                }
            }

            item {
                when (val resolved = item) {
                    is CollectHistoryItem -> {
                        if (!resolved.lastError.isNullOrBlank()) {
                            Box(modifier = Modifier.padding(horizontal = Dimens.Space.medium, vertical = Dimens.Space.small)) {
                                ErrorInfo(message = resolved.lastError!!, attempts = resolved.attempts)
                            }
                        }
                    }

                    else -> { /* no-op */
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
                    is CollectHistoryItem -> {
                        items(resolved.metadata, key = { it.invoiceNumber }) { line ->
                            CollectMetadataRowCard(
                                metadata = line,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }

                    else -> { /* optional empty state */
                    }
                }
            }
        }
    }
}


