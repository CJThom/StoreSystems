package com.gpcasiapac.storesystems.feature.collect.presentation.destination.workorderdetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun WorkOrderDetailsScreenDestination(
    invoiceNumber: String,
    viewModel: WorkOrderDetailsScreenViewModel = koinViewModel { parametersOf(invoiceNumber) },
    onOutcome: (outcome: WorkOrderDetailsScreenContract.Effect.Outcome) -> Unit,
) {
    WorkOrderDetailsScreen(
        state = viewModel.viewState.collectAsState().value,
        onEventSent = viewModel::setEvent,
        effectFlow = viewModel.effect,
        onOutcome = onOutcome
    )
}