package com.gpcasiapac.storesystems.feature.login.presentation.entry

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.entry
import com.gpcasiapac.storesystems.common.presentation.navigation.FeatureEntriesRegistrar
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureDestination
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureEntry
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginDestination
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginScreenContract
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginViewModel
import com.gpcasiapac.storesystems.feature.login.presentation.otp_screen.OtpScreen
import org.koin.compose.viewmodel.koinViewModel

/**
 * Android-specific implementation that also registers Navigation3 entries.
 */
class LoginFeatureEntryAndroidImpl : LoginFeatureEntry {

    @Composable
    override fun Host(onSuccess: () -> Unit) {
        LoginHost(onSuccess = onSuccess)
    }

    override fun registerEntries(
        registrar: FeatureEntriesRegistrar,
        onLoggedIn: () -> Unit
    ) {
        registrar.builder.apply {
            entry<LoginFeatureDestination.Login> {
                val viewModel: LoginViewModel = koinViewModel()
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
                    onBack = { registrar.pop() },
                    onOtpSuccess = { onLoggedIn() }
                )
            }
        }
    }
}
