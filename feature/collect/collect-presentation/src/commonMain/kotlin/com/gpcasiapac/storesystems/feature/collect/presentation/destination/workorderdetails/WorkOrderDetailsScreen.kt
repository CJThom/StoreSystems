package com.gpcasiapac.storesystems.feature.collect.presentation.destination.workorderdetails

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
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
import com.gpcasiapac.storesystems.feature.collect.presentation.component.OrderDetailsLarge
import com.gpcasiapac.storesystems.foundation.component.MBoltAppBar
import com.gpcasiapac.storesystems.foundation.component.TopBarTitle
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WorkOrderDetailsScreen(
    state: WorkOrderDetailsScreenContract.State,
    onEventSent: (event: WorkOrderDetailsScreenContract.Event) -> Unit,
    effectFlow: Flow<WorkOrderDetailsScreenContract.Effect>?,
    onOutcome: (outcome: WorkOrderDetailsScreenContract.Effect.Outcome) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(effectFlow) {
        effectFlow?.collectLatest { effect ->
            when (effect) {
                is WorkOrderDetailsScreenContract.Effect.ShowToast -> snackbarHostState.showSnackbar(
                    effect.message, duration = SnackbarDuration.Short
                )
                is WorkOrderDetailsScreenContract.Effect.ShowError -> snackbarHostState.showSnackbar(
                    effect.error
                )
                is WorkOrderDetailsScreenContract.Effect.Outcome -> onOutcome(effect)
            }
        }
    }

    Scaffold(
        topBar = {
            MBoltAppBar(
                title = { TopBarTitle("Order Details") },
                navigationIcon = {
                    IconButton(onClick = { onEventSent(WorkOrderDetailsScreenContract.Event.Back) }) {
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
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                Text(text = state.error)
            }
        } else if (state.order != null) {
            Column(
                modifier = Modifier
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                OrderDetailsLarge(
                    orderState = state.order
                )
            }
        }
    }
}