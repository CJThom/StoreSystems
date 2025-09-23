package com.gpcasiapac.storesystems.feature.collect.presentation.orders

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState

@Composable
fun OrdersDestination(
    viewModel: OrdersViewModel,
    onNavigationRequested: (navigationEffect: OrdersScreenContract.Effect.Navigation) -> Unit = {}
) {
    OrdersScreen(
        state = viewModel.viewState.collectAsState().value,
        onEventSent = { event -> viewModel.setEvent(event) },
        effectFlow = viewModel.effect,
        onNavigationRequested = onNavigationRequested,
    )
}
