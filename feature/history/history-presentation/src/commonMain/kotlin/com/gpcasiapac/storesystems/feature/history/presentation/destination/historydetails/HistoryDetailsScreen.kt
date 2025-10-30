package com.gpcasiapac.storesystems.feature.history.presentation.destination.historydetails

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.history.presentation.composable.HistoryItemCard
import com.gpcasiapac.storesystems.feature.history.presentation.destination.history.HistoryScreenContract
import com.gpcasiapac.storesystems.foundation.component.CheckboxCard
import com.gpcasiapac.storesystems.foundation.component.MBoltAppBar
import com.gpcasiapac.storesystems.foundation.component.TopBarTitle
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryDetailsScreen(
    state: HistoryDetailsScreenContract.State,
    onEvent: (HistoryDetailsScreenContract.Event) -> Unit,
    effectFlow: Flow<HistoryDetailsScreenContract.Effect>,
    onOutcome: (HistoryDetailsScreenContract.Effect.Outcome) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(effectFlow) {
        effectFlow.collectLatest { effect ->
            when (effect) {
                is HistoryDetailsScreenContract.Effect.ShowError -> snackbarHostState.showSnackbar(effect.message)
                is HistoryDetailsScreenContract.Effect.Outcome -> onOutcome(effect)
            }
        }
    }

    Scaffold(
        topBar = {
            MBoltAppBar(
                title = { TopBarTitle( "History Details" ) },
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
        when {
            state.isLoading -> {
                CircularProgressIndicator()
                Text("Loading…", modifier = Modifier.padding(top = 8.dp))
            }
            state.error != null -> {
                Text(state.error, color = MaterialTheme.colorScheme.error)
            }
            else -> {
                LazyColumn {
                    items(state.items, key = { it.id }) { item ->
                            HistoryItemCard(
                                item = item,
                                onClick = { /* can navigate deeper if needed */ },
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                    }
                }
            }
        }
    }
}
