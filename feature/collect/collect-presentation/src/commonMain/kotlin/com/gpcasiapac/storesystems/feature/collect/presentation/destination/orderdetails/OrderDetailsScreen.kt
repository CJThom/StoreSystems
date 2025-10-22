package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetails

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.FlexibleBottomAppBar
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
import androidx.compose.ui.tooling.preview.Preview
import com.gpcasiapac.storesystems.feature.collect.presentation.component.OrderDetailsLarge
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.model.CollectOrderWithCustomerWithLineItemsState
import com.gpcasiapac.storesystems.foundation.component.MBoltAppBar
import com.gpcasiapac.storesystems.foundation.component.TopBarTitle
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import com.gpcasiapac.storesystems.common.feedback.sound.SoundPlayer
import com.gpcasiapac.storesystems.common.feedback.haptic.HapticPerformer
import org.koin.compose.koinInject
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class, ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun OrderDetailsScreen(
    state: OrderDetailsScreenContract.State,
    onEventSent: (event: OrderDetailsScreenContract.Event) -> Unit,
    effectFlow: Flow<OrderDetailsScreenContract.Effect>?,
    onOutcome: (outcome: OrderDetailsScreenContract.Effect.Outcome) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }
    // Platform feedback
    val soundPlayer: SoundPlayer = koinInject()
    val hapticPerformer: HapticPerformer = koinInject()

    LaunchedEffect(effectFlow) {
        effectFlow?.collectLatest { effect ->
            when (effect) {
                is OrderDetailsScreenContract.Effect.ShowToast -> snackbarHostState.showSnackbar(
                    effect.message, duration = SnackbarDuration.Short
                )

                is OrderDetailsScreenContract.Effect.ShowError -> snackbarHostState.showSnackbar(
                    effect.error
                )

                is OrderDetailsScreenContract.Effect.ShowSnackbar -> {
                    snackbarHostState.showSnackbar(
                        message = effect.message,
                        actionLabel = effect.actionLabel,
                        duration = effect.duration
                    )
                }

                is OrderDetailsScreenContract.Effect.PlaySound -> {
                    soundPlayer.play(effect.soundEffect)
                }

                is OrderDetailsScreenContract.Effect.PlayHaptic -> {
                    hapticPerformer.perform(effect.hapticEffect)
                }

                is OrderDetailsScreenContract.Effect.Outcome -> onOutcome(effect)
            }
        }
    }

    Scaffold(
        topBar = {
            MBoltAppBar(
                title = { TopBarTitle("Order Details") },
                navigationIcon = {
                    IconButton(onClick = { onEventSent(OrderDetailsScreenContract.Event.Back) }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = MaterialTheme.colorScheme.onPrimary
                        )
                    }
                }
            )
        },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        bottomBar = {
            FlexibleBottomAppBar(
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        onEventSent(OrderDetailsScreenContract.Event.Select)
                    }
                ) {
                    Text("SELECT")
                }
            }
        }
    ) { padding ->
        if (state.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        } else if (state.error != null) {
            Box(
                modifier = Modifier.fillMaxSize().padding(padding),
                contentAlignment = Alignment.Center
            ) {
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

@Preview
@Composable
private fun OrderDetailsScreenPreview() {
    GPCTheme {
        OrderDetailsScreen(
            state = OrderDetailsScreenContract.State(
                isLoading = false,
                error = null,
                order = CollectOrderWithCustomerWithLineItemsState.placeholder()
            ),
            onEventSent = {},
            effectFlow = null,
            onOutcome = {}
        )
    }
}

@Preview
@Composable
private fun OrderDetailsScreenLoadingPreview() {
    GPCTheme {
        OrderDetailsScreen(
            state = OrderDetailsScreenContract.State(
                isLoading = true,
                error = null,
                order = null
            ),
            onEventSent = {},
            effectFlow = null,
            onOutcome = {}
        )
    }
}