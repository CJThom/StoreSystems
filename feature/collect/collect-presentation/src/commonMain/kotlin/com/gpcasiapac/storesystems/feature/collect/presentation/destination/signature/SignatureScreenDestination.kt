package com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.LaunchedEffect
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SignatureScreenDestination(
    customerName: String,
    signatureScreenViewModel: SignatureScreenViewModel = koinViewModel(),
    onOutcome: (outcome: SignatureScreenContract.Effect.Outcome) -> Unit,
) {
    LaunchedEffect(customerName) {
        signatureScreenViewModel.setEvent(SignatureScreenContract.Event.SetCustomerName(customerName))
    }
    SignatureScreen(
        state = signatureScreenViewModel.viewState.collectAsState().value,
        onEventSent = { event -> signatureScreenViewModel.setEvent(event) },
        effectFlow = signatureScreenViewModel.effect,
        onOutcome = onOutcome,
    )
}
