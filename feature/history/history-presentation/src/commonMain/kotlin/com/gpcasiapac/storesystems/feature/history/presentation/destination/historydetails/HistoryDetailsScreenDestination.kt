package com.gpcasiapac.storesystems.feature.history.presentation.destination.historydetails

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import com.gpcasiapac.storesystems.feature.history.api.HistoryType
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HistoryDetailsScreenDestination(
    type: HistoryType,
    id: String,
    viewModel: HistoryDetailsScreenViewModel = koinViewModel(),
    onOutcome: (HistoryDetailsScreenContract.Effect.Outcome) -> Unit,
) {
    HistoryDetailsScreen(
        id  = id,
        type = type,
        state = viewModel.viewState.collectAsState().value,
        onEvent = viewModel::setEvent,
        effectFlow = viewModel.effect,
        onOutcome = onOutcome,
    )
}
