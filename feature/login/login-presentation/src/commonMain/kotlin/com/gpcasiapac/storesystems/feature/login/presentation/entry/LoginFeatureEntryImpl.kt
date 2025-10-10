package com.gpcasiapac.storesystems.feature.login.presentation.entry

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.gpcasiapac.storesystems.feature.login.api.LoginExternalOutcome
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureDestination
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureEntry
import com.gpcasiapac.storesystems.feature.login.api.LoginOutcome
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginDestination
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginScreenContract
import com.gpcasiapac.storesystems.feature.login.presentation.mfa_screen.MfaScreen
import com.gpcasiapac.storesystems.feature.login.presentation.navigation.LoginNavigationContract
import com.gpcasiapac.storesystems.feature.login.presentation.navigation.LoginNavigationViewModel
import org.koin.compose.viewmodel.koinViewModel

/**
 * Android-specific implementation that also registers Navigation3 entries.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
class LoginFeatureEntryImpl : LoginFeatureEntry {

    @Composable
    override fun Host(
        onExternalOutcome: (LoginExternalOutcome) -> Unit,
    ) {

        val loginNavigationViewModel: LoginNavigationViewModel = koinViewModel()
        val state by loginNavigationViewModel.viewState.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            loginNavigationViewModel.effect.collect { effect ->
                when (effect) {
                    is LoginNavigationContract.Effect.ExternalOutcome -> onExternalOutcome(effect.outcome)
                }
            }
        }

        NavDisplay(
            backStack = state.stack,
            onBack = {
                loginNavigationViewModel.setEvent(LoginNavigationContract.Event.PopBack)
            },
            transitionSpec = { fadeIn() togetherWith fadeOut() },
            popTransitionSpec = { fadeIn() togetherWith fadeOut() },
            predictivePopTransitionSpec = { fadeIn() togetherWith fadeOut() },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                registerEntries(
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

    override fun registerEntries(
        builder: EntryProviderScope<NavKey>,
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

}