package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OrderListScreenDestination(
    orderListScreenViewModel: OrderListScreenViewModel = koinViewModel(),
    onOutcome: (outcome: OrderListScreenContract.Effect.Outcome) -> Unit
) {
//    OrderListScreenPlaceholder(
//        state = orderListScreenViewModel.viewState.collectAsState().value,
//        onEventSent = { event -> orderListScreenViewModel.setEvent(event) },
//        effectFlow = orderListScreenViewModel.effect,
//        onOutcome = onOutcome
//    )
    OrderListScreen(
        state = orderListScreenViewModel.viewState.collectAsState().value,
        onEventSent = { event -> orderListScreenViewModel.setEvent(event) },
        effectFlow = orderListScreenViewModel.effect,
        onOutcome = onOutcome
    )
}
