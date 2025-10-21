package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import co.touchlab.kermit.Logger
import com.gpcasiapac.storesystems.common.scanning.ScanEventsRegistry
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.search.SearchViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun OrderListScreenDestination(
    orderListScreenViewModel: OrderListScreenViewModel = koinViewModel(),
    searchViewModel: SearchViewModel = koinViewModel(),
    onOutcome: (outcome: OrderListScreenContract.Effect.Outcome) -> Unit
) {
    val log = Logger.withTag("OrderListScreenDestination")
    // Collect scanner results: when a barcode/QR is scanned, navigate to order details
    LaunchedEffect(Unit) {
        log.i { "Starting scan collection for OrderList screen" }
        ScanEventsRegistry.provider?.invoke()?.collectLatest { scan ->
            log.i { "Forwarding scan to VM: '${scan.text.take(64)}'" }
            orderListScreenViewModel.setEvent(OrderListScreenContract.Event.OpenOrder(scan.text))
        }
    }

    OrderListScreen(
        state = orderListScreenViewModel.viewState.collectAsState().value,
        searchState = searchViewModel.viewState.collectAsState().value,
        onEventSent = { event -> orderListScreenViewModel.setEvent(event) },
        onSearchEventSent = { event -> searchViewModel.setEvent(event) },
        effectFlow = orderListScreenViewModel.effect,
        onOutcome = onOutcome,
        searchEffectFlow = searchViewModel.effect,
    )
}
