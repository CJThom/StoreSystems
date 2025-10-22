package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.search.SearchViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OrderFulfilmentScreenDestination(
    orderFulfilmentScreenViewModel: OrderFulfilmentScreenViewModel = koinViewModel(),
    searchViewModel: SearchViewModel = koinViewModel(),
    autoSelectOnScan: Boolean = true,
    onOutcome: (outcome: OrderFulfilmentScreenContract.Effect.Outcome) -> Unit,
) {
    // Collect scanner results: on scan, either auto-select into work order or navigate to details
    androidx.compose.runtime.LaunchedEffect(Unit) {
        co.touchlab.kermit.Logger.withTag("OrderFulfilmentScreenDestination").i { "Starting scan collection for Fulfilment screen (autoSelect=$autoSelectOnScan)" }
        com.gpcasiapac.storesystems.common.scanning.ScanEventsRegistry.provider?.invoke()?.collectLatest { scan ->
            orderFulfilmentScreenViewModel.setEvent(
                OrderFulfilmentScreenContract.Event.ScanInvoice(scan.text, autoSelectOnScan)
            )
        }
    }

    OrderFulfilmentScreen(
        state = orderFulfilmentScreenViewModel.viewState.collectAsState().value,
        searchState = searchViewModel.viewState.collectAsState().value,
        onEventSent = { event -> orderFulfilmentScreenViewModel.setEvent(event) },
        onSearchEventSent = { event -> searchViewModel.setEvent(event) },
        effectFlow = orderFulfilmentScreenViewModel.effect,
        onOutcome = onOutcome
    )
}