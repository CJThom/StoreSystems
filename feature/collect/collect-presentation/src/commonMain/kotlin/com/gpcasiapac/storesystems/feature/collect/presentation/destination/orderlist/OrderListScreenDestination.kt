package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import co.touchlab.kermit.Logger
import com.gpcasiapac.storesystems.common.scanning.ScanEventsRegistry
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.search.SearchViewModel
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.viewmodel.koinViewModel
import org.koin.compose.koinInject
import com.gpcasiapac.storesystems.common.feedback.sound.SoundPlayer
import com.gpcasiapac.storesystems.common.feedback.haptic.HapticPerformer

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
            log.i { "Scan received for invoice='${scan.text.take(64)}' - dispatching ScanInvoice" }
            orderListScreenViewModel.setEvent(OrderListScreenContract.Event.ScanInvoice(scan.text))
        }
    }

    val soundPlayer: SoundPlayer = koinInject()
    val hapticPerformer: HapticPerformer = koinInject()

    OrderListScreen(
        state = orderListScreenViewModel.viewState.collectAsState().value,
        searchState = searchViewModel.viewState.collectAsState().value,
        onEventSent = { event -> orderListScreenViewModel.setEvent(event) },
        onSearchEventSent = { event -> searchViewModel.setEvent(event) },
        effectFlow = orderListScreenViewModel.effect,
        onOutcome = onOutcome,
        soundPlayer = soundPlayer,
        hapticPerformer = hapticPerformer,
        searchEffectFlow = searchViewModel.effect,
    )
}
