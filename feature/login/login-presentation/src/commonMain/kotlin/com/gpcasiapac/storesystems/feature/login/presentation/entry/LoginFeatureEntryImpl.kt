package com.gpcasiapac.storesystems.feature.login.presentation.entry

import androidx.compose.runtime.Composable
import com.gpcasiapac.storesystems.common.presentation.navigation.FeatureEntriesRegistrar
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureEntry
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginDestination
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginScreenContract
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Common implementation of LoginFeatureEntry.
 * Provides a Composable Login() and a no-op registerEntries() for non-Android targets.
 */
class LoginFeatureEntryImpl : LoginFeatureEntry {

    @Composable
    override fun Login(onLoggedIn: () -> Unit) {
        val viewModel: LoginViewModel = koinViewModel()
        LoginDestination(
            loginViewModel = viewModel,
            onNavigationRequested = { navigationEffect ->
                when (navigationEffect) {
                    is LoginScreenContract.Effect.Navigation.NavigateToHome -> onLoggedIn()
                    is LoginScreenContract.Effect.Navigation.NavigateToOtp -> { /* single-screen no-op */ }
                }
            }
        )
    }

    @Composable
    override fun Host(onSuccess: () -> Unit) {
        Login(onLoggedIn = onSuccess)
    }

    override fun registerEntries(
        registrar: FeatureEntriesRegistrar,
        onLoggedIn: () -> Unit
    ) { /* no-op on non-Android targets for now */ }
}
