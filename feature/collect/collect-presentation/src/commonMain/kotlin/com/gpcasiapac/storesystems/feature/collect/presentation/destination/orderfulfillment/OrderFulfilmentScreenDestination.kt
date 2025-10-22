package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import co.touchlab.kermit.Logger
import com.gpcasiapac.storesystems.common.scanning.ScanEventsRegistry
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
    LaunchedEffect(Unit) {
        Logger.withTag("OrderFulfilmentScreenDestination").i { "Starting scan collection for Fulfilment screen (autoSelect=$autoSelectOnScan)" }
        ScanEventsRegistry.provider?.invoke()?.collectLatest { scan ->
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