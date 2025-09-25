package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OrderDetailScreenDestination(
    orderId: String,
    orderDetailScreenViewModel: OrderDetailScreenViewModel = koinViewModel(),
    onOutcome: (outcome: OrderDetailScreenContract.Effect.Outcome) -> Unit,
) {
    // Provide args before first collection so MVIViewModel.onStart can trigger initial load
    orderDetailScreenViewModel.setArgs(orderId)

    OrderDetailScreen(
        state = orderDetailScreenViewModel.viewState.collectAsState().value,
        onEventSent = { event -> orderDetailScreenViewModel.setEvent(event) },
        effectFlow = orderDetailScreenViewModel.effect,
        onOutcome = onOutcome,
    )
}
