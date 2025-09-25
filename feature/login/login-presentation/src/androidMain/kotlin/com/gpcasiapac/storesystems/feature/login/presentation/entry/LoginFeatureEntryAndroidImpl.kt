package com.gpcasiapac.storesystems.feature.login.presentation.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.gpcasiapac.storesystems.feature.login.api.LoginExternalOutcome
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureDestination
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureEntry
import com.gpcasiapac.storesystems.feature.login.api.LoginOutcome
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginDestination
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginScreenContract
import com.gpcasiapac.storesystems.feature.login.presentation.navigation.LoginNavigationContract
import com.gpcasiapac.storesystems.feature.login.presentation.navigation.LoginNavigationViewModel
import com.gpcasiapac.storesystems.feature.login.presentation.mfa_screen.MfaScreen
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Android-specific implementation that also registers Navigation3 entries.
 */
class LoginFeatureEntryAndroidImpl : LoginFeatureEntry {

    override fun registerEntries(
        builder: EntryProviderBuilder<NavKey>,
        onOutcome: (LoginOutcome) -> Unit,
    ) {
        builder.apply {
            entry<LoginFeatureDestination.Login> {
                LoginDestination(
                    onOutcome = { outcome ->
                        when (outcome) {
                            is LoginScreenContract.Effect.Outcome.LoginCompleted -> {
                                onOutcome(LoginOutcome.LoginCompleted)
                            }

                            is LoginScreenContract.Effect.Outcome.MfaRequired -> {
                                onOutcome(LoginOutcome.MfaRequired(outcome.userId))
                            }
                        }
                    }
                )
            }

            entry<LoginFeatureDestination.Mfa> { otp ->
                MfaScreen(
                    userId = otp.userId,
                    onBack = { onOutcome(LoginOutcome.Back) },
                    onOtpSuccess = { onOutcome(LoginOutcome.LoginCompleted) }
                )
            }
        }
    }

    @Composable
    override fun Host(
        onExternalOutcome: (LoginExternalOutcome) -> Unit,
    ) {

        val loginNavigationViewModel: LoginNavigationViewModel = koinViewModel()
        val state by loginNavigationViewModel.viewState.collectAsState()

        val loginEntry: LoginFeatureEntry = koinInject()

        LaunchedEffect(Unit) {
            loginNavigationViewModel.effect.collect { effect ->
                when (effect) {
                    is LoginNavigationContract.Effect.ExternalOutcome -> onExternalOutcome(effect.outcome)
                }
            }
        }

        NavDisplay(
            backStack = state.stack,
            onBack = { count ->
                loginNavigationViewModel.setEvent(
                    LoginNavigationContract.Event.PopBack(count)
                )
            },
            entryDecorators = listOf(
                rememberSavedStateNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                loginEntry.registerEntries(
                    builder = this,
                    onOutcome = { outcome ->
                        loginNavigationViewModel.setEvent(
                            LoginNavigationContract.Event.Outcome(outcome)
                        )
                    }
                )
            }
        )

    }

}
