package com.gpcasiapac.storesystems.feature.login.presentation.entry

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureDestination
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureEntry
import com.gpcasiapac.storesystems.feature.login.api.LoginOutcome
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginDestination
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginScreenContract
import com.gpcasiapac.storesystems.feature.login.presentation.otp_screen.OtpScreen

/**
 * Android-specific implementation that also registers Navigation3 entries.
 */
class LoginFeatureEntryAndroidImpl : LoginFeatureEntry {

    @Composable
    override fun Host(onComplete: () -> Unit) {
        LoginHost(onComplete = onComplete)
    }

    override fun registerEntries(
        builder: EntryProviderBuilder<NavKey>,
        onOutcome: (LoginOutcome) -> Unit,
    ) {
        builder.apply {
            entry<LoginFeatureDestination.Login> {
                LoginDestination(
                    onNavigationRequested = { outcome ->
                        when (outcome) {
                            is LoginScreenContract.Effect.Navigation.LoginCompleted -> onOutcome(
                                LoginOutcome.LoginCompleted
                            )
                            is LoginScreenContract.Effect.Navigation.MfaRequired -> onOutcome(
                                LoginOutcome.MfaRequired(outcome.userId)
                            )
                        }
                    }
                )
            }

            entry<LoginFeatureDestination.Otp> { otp ->
                OtpScreen(
                    userId = otp.userId,
                    onBack = { onOutcome(LoginOutcome.Back) },
                    onOtpSuccess = { onOutcome(LoginOutcome.LoginCompleted) }
                )
            }
        }
    }
}
