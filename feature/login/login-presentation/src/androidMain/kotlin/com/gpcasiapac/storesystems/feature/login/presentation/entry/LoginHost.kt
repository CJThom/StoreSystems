package com.gpcasiapac.storesystems.feature.login.presentation.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginDestination
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginScreenContract
import com.gpcasiapac.storesystems.feature.login.presentation.navigation.LoginNavContract
import com.gpcasiapac.storesystems.feature.login.presentation.navigation.LoginNavigationViewModel
import com.gpcasiapac.storesystems.feature.login.presentation.navigation.LoginStep
import com.gpcasiapac.storesystems.feature.login.presentation.otp_screen.OtpScreen
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun LoginHost(
    loginNavigationViewModel: LoginNavigationViewModel = koinViewModel(),
    onComplete: () -> Unit,
) {

    val state by loginNavigationViewModel.viewState.collectAsState()

    NavDisplay(
        backStack = state.stack,
        onBack = { count -> loginNavigationViewModel.setEvent(LoginNavContract.Event.PopBack(count)) },
        entryDecorators = listOf(
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<LoginStep.Login> {
                LoginDestination(
                    onNavigationRequested = { outcome ->
                        when (outcome) {
                            is LoginScreenContract.Effect.Navigation.MfaRequired -> {
                                loginNavigationViewModel.setEvent(
                                    LoginNavContract.Event.MfaRequired(
                                        outcome.userId
                                    )
                                )
                            }

                            is LoginScreenContract.Effect.Navigation.LoginCompleted -> {
                                loginNavigationViewModel.setEvent(LoginNavContract.Event.LoginCompleted)
                            }
                        }
                    }
                )
            }
            entry<LoginStep.Mfa> { otp ->
                OtpScreen(
                    userId = otp.userId,
                    onBack = { loginNavigationViewModel.setEvent(LoginNavContract.Event.PopBack()) },
                    onOtpSuccess = { loginNavigationViewModel.setEvent(LoginNavContract.Event.LoginCompleted) },
                )
            }
            entry<LoginStep.Mfa_V2> { otp ->
                OtpScreen(
                    userId = otp.userId,
                    onBack = { loginNavigationViewModel.setEvent(LoginNavContract.Event.PopBack()) },
                    onOtpSuccess = { loginNavigationViewModel.setEvent(LoginNavContract.Event.LoginCompleted) },
                )
            }
            entry<LoginStep.Complete> {
                LaunchedEffect(Unit) { onComplete() }
            }
        }
    )
}
