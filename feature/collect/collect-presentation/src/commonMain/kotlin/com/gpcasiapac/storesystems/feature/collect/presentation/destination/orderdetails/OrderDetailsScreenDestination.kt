package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.workorderdetails.WorkOrderDetailsScreen
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.workorderdetails.WorkOrderDetailsScreenContract
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.workorderdetails.WorkOrderDetailsScreenViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun OrderDetailsScreenDestination(
    invoiceNumber: String,
    viewModel: OrderDetailsScreenViewModel = koinViewModel { parametersOf(invoiceNumber) },
    onOutcome: (outcome: OrderDetailsScreenContract.Effect.Outcome) -> Unit,
) {
    OrderDetailsScreen(
        state = viewModel.viewState.collectAsState().value,
        onEventSent = viewModel::setEvent,
        effectFlow = viewModel.effect,
        onOutcome = onOutcome
    )
}