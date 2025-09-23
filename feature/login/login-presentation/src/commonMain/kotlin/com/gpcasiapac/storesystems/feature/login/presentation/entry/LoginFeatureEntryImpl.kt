package com.gpcasiapac.storesystems.feature.login.presentation.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.gpcasiapac.storesystems.common.feature_flags.FeatureFlags
import com.gpcasiapac.storesystems.common.presentation.navigation.FeatureEntriesRegistrar
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureEntry
import com.gpcasiapac.storesystems.feature.login.api.LoginService
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginDestination
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginScreenContract
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginViewModel
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Common implementation of LoginFeatureEntry.
 * Provides a Composable Login() and a no-op registerEntries() for non-Android targets.
 */
class LoginFeatureEntryImpl : LoginFeatureEntry, KoinComponent {
    private val loginService: LoginService by inject()
    private val flags: FeatureFlags by inject()

    @Composable
    override fun Login(onLoggedIn: () -> Unit) {
        val viewModel = remember(loginService, flags) { LoginViewModel(loginService, flags) }
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
