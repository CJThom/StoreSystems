package com.gpcasiapac.storesystems.feature.history.presentation.destination.history

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun HistoryScreenDestination(
    viewModel: HistoryScreenViewModel = koinViewModel(),
    onOutcome: (outcome: HistoryScreenContract.Effect.Outcome) -> Unit,
) {
    HistoryScreen(
        state = viewModel.viewState.collectAsState().value,
        onEventSent = { event -> viewModel.setEvent(event) },
        effectFlow = viewModel.effect,
        onOutcome = onOutcome,
    )
}
