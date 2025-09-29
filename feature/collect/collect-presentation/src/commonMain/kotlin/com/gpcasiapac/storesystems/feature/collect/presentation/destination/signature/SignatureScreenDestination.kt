package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignatureScreenDestination(
    signatureScreenViewModel: SignatureScreenViewModel = koinViewModel(),
    onOutcome: (outcome: SignatureScreenContract.Effect.Outcome) -> Unit,
) {
    SignatureScreen(
        state = signatureScreenViewModel.viewState.collectAsState().value,
        onEventSent = { event -> signatureScreenViewModel.setEvent(event) },
        effectFlow = signatureScreenViewModel.effect,
        onOutcome = onOutcome,
    )
}
