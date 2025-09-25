package com.gpcasiapac.storesystems.feature.collect.presentation.orders

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OrdersDestination(
    ordersViewModel: OrdersViewModel = koinViewModel(),
    onOutcome: (outcome: OrdersScreenContract.Effect.Outcome) -> Unit
) {
    OrdersScreen(
        state = ordersViewModel.viewState.collectAsState().value,
        onEventSent = { event -> ordersViewModel.setEvent(event) },
        effectFlow = ordersViewModel.effect,
        onOutcome = onOutcome
    )
}
