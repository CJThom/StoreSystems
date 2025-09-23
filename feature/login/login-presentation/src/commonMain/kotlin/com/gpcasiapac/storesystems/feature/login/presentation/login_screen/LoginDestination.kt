package com.gpcasiapac.storesystems.feature.login.presentation.login_screen

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import com.gpcasiapac.storesystems.feature.login.presentation.navigation.LoginNavViewModel
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginDestination(
    loginViewModel: LoginViewModel,
    onNavigationRequested: (navigationEffect: LoginScreenContract.Effect.Navigation) -> Unit = {}
) {
    LoginScreen(
        state = loginViewModel.viewState.collectAsState().value,
        onEventSent = { event -> loginViewModel.setEvent(event) },
        effectFlow = loginViewModel.effect,
        onNavigationRequested = onNavigationRequested
    )
}
