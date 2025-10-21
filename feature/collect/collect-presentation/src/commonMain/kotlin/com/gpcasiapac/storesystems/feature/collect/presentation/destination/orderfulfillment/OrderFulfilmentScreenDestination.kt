package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.search.SearchViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OrderFulfilmentScreenDestination(
    orderFulfilmentScreenViewModel: OrderFulfilmentScreenViewModel = koinViewModel(),
    searchViewModel: SearchViewModel = koinViewModel(),
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