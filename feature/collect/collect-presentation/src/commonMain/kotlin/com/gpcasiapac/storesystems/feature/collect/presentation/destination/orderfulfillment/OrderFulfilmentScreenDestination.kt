package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OrderFulfilmentScreenDestination(
    orderFulfilmentScreenViewModel: OrderFulfilmentScreenViewModel = koinViewModel(),
    onOutcome: (outcome: OrderFulfilmentScreenContract.Effect.Outcome) -> Unit,
) {
    OrderFulfilmentScreen(
        state = orderFulfilmentScreenViewModel.viewState.collectAsState().value,
        onEventSent = { event -> orderFulfilmentScreenViewModel.setEvent(event) },
        effectFlow = orderFulfilmentScreenViewModel.effect,
        onOutcome = onOutcome,
    )
}