package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OrderFulfilmentScreenDestination(
    orderFulfilmentScreenViewModel: OrderFulfilmentScreenViewModel = koinViewModel(),
    searchViewModel: com.gpcasiapac.storesystems.feature.collect.presentation.search.SearchViewModel = koinViewModel(),
    onOutcome: (outcome: OrderFulfilmentScreenContract.Effect.Outcome) -> Unit,
) {
    OrderFulfilmentScreen(
        state = orderFulfilmentScreenViewModel.viewState.collectAsState().value,
        searchState = searchViewModel.viewState.collectAsState().value,
        onEventSent = { event -> orderFulfilmentScreenViewModel.setEvent(event) },
        onSearchEventSent = { event -> searchViewModel.setEvent(event) },
        effectFlow = orderFulfilmentScreenViewModel.effect,
        onOutcome = onOutcome
    )
}