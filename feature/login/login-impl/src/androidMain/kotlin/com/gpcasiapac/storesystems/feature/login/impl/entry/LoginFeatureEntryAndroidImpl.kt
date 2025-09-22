package com.gpcasiapac.storesystems.feature.login.impl.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation3.runtime.entry
import com.gpcasiapac.storesystems.common.feature_flags.FeatureFlags
import com.gpcasiapac.storesystems.common.presentation.navigation.FeatureEntriesRegistrar
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureDestination
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureEntry
import com.gpcasiapac.storesystems.feature.login.api.LoginService
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginDestination
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginScreenContract
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginViewModel
import com.gpcasiapac.storesystems.feature.login.presentation.otp_screen.OtpScreen
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

/**
 * Android-specific implementation that also registers Navigation3 entries.
 */
class LoginFeatureEntryAndroidImpl : LoginFeatureEntry, KoinComponent {
    private val loginService: LoginService by inject()
    private val flags: FeatureFlags by inject()

    @Composable
    override fun Login(modifier: Modifier, onLoggedIn: () -> Unit) {
        val viewModel = remember(loginService, flags) { LoginViewModel(loginService, flags) }
        LoginDestination(
            loginViewModel = viewModel,
            onNavigationRequested = { navigationEffect ->
                when (navigationEffect) {
                    is LoginScreenContract.Effect.Navigation.NavigateToHome -> onLoggedIn()
                    is LoginScreenContract.Effect.Navigation.NavigateToOtp -> { /* handled via registerEntries */ }
                }
            }
        )
    }

    @Composable
    override fun Host() {
        Login(modifier = Modifier) {}
    }

    override fun registerEntries(
        registrar: FeatureEntriesRegistrar,
        onLoggedIn: () -> Unit
    ) {
        registrar.builder.apply {
            entry<LoginFeatureDestination.Login> {
                val viewModel = LoginViewModel(loginService, flags)
                LoginDestination(
                    loginViewModel = viewModel,
                    onNavigationRequested = { nav ->
                        when (nav) {
                            is LoginScreenContract.Effect.Navigation.NavigateToHome -> onLoggedIn()
                            is LoginScreenContract.Effect.Navigation.NavigateToOtp -> {
                                registrar.push(LoginFeatureDestination.Otp(nav.userId))
                            }
                        }
                    }
                )
            }

            entry<LoginFeatureDestination.Otp> { otp ->
                OtpScreen(
                    userId = otp.userId,
                    modifier = Modifier,
                    onBack = { registrar.pop() },
                    onOtpSuccess = { onLoggedIn() }
                )
            }
        }
    }
}
