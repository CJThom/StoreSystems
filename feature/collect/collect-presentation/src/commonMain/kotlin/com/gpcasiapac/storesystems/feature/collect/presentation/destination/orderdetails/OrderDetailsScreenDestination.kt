package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import co.touchlab.kermit.Logger
import com.gpcasiapac.storesystems.common.feedback.haptic.HapticPerformer
import com.gpcasiapac.storesystems.common.feedback.sound.SoundPlayer
import com.gpcasiapac.storesystems.common.scanning.ScanEventsRegistry
import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import kotlinx.coroutines.flow.collectLatest
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun OrderDetailsScreenDestination(
    invoiceNumber: InvoiceNumber,
    viewModel: OrderDetailsScreenViewModel = koinViewModel { parametersOf(invoiceNumber) },
    onOutcome: (outcome: OrderDetailsScreenContract.Effect.Outcome) -> Unit,
) {
    // Collect scans and forward to VM
    LaunchedEffect(Unit) {
        Logger.withTag("OrderDetailsScreenDestination").i { "Starting scan collection for OrderDetails screen" }
        ScanEventsRegistry.provider?.invoke()?.collectLatest { scan ->
            viewModel.setEvent(OrderDetailsScreenContract.Event.Scan(scan.text))
        }
    }

    val soundPlayer: SoundPlayer = koinInject()
    val hapticPerformer: HapticPerformer = koinInject()

    OrderDetailsScreen(
        state = viewModel.viewState.collectAsState().value,
        onEventSent = viewModel::setEvent,
        effectFlow = viewModel.effect,
        onOutcome = onOutcome
    )
}