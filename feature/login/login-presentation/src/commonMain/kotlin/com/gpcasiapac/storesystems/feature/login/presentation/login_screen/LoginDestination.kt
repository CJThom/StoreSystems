package com.gpcasiapac.storesystems.feature.login.presentation.login_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginDestination(
    loginViewModel: LoginViewModel = koinViewModel(),
    onExternalOutcome: (outcome: LoginScreenContract.Effect.Outcome) -> Unit
) {
    LoginScreen(
        state = loginViewModel.viewState.collectAsState().value,
        onEventSent = { event -> loginViewModel.setEvent(event) },
        effectFlow = loginViewModel.effect,
        onExternalOutcome = onExternalOutcome
    )
}
