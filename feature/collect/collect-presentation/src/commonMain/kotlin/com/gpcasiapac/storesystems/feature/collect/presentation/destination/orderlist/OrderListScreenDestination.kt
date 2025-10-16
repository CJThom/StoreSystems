package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OrderListScreenDestination(
    orderListScreenViewModel: OrderListScreenViewModel = koinViewModel(),
    searchViewModel: com.gpcasiapac.storesystems.feature.collect.presentation.search.SearchViewModel = koinViewModel(),
    onOutcome: (outcome: OrderListScreenContract.Effect.Outcome) -> Unit
) {
    OrderListScreen(
        state = orderListScreenViewModel.viewState.collectAsState().value,
        searchState = searchViewModel.viewState.collectAsState().value,
        onEventSent = { event -> orderListScreenViewModel.setEvent(event) },
        onSearchEventSent = { event -> searchViewModel.setEvent(event) },
        effectFlow = orderListScreenViewModel.effect,
        onOutcome = onOutcome
    )
}
