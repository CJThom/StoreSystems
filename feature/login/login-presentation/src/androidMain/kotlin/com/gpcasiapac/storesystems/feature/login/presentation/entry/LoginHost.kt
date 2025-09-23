package com.gpcasiapac.storesystems.feature.login.presentation.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.gpcasiapac.storesystems.common.presentation.navigation.NavEvent
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginDestination
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginScreenContract
import com.gpcasiapac.storesystems.feature.login.presentation.login_screen.LoginViewModel
import com.gpcasiapac.storesystems.feature.login.presentation.navigation.LoginNavViewModel
import com.gpcasiapac.storesystems.feature.login.presentation.navigation.LoginStep
import com.gpcasiapac.storesystems.feature.login.presentation.otp_screen.OtpScreen
import org.koin.compose.viewmodel.koinViewModel


object Thing : NavKey

@Composable
fun LoginHost(
    onSuccess: () -> Unit,
) {
    val navVm: LoginNavViewModel = koinViewModel()
    val state by navVm.viewState.collectAsState()

    NavDisplay(
        backStack = state.stack,
        onBack = { count -> navVm.setEvent(NavEvent.Pop(count)) },
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            entry<LoginStep.Login> {
                val viewModel: LoginViewModel = koinViewModel()
                LoginDestination(
                    loginViewModel = viewModel,
                    onNavigationRequested = { nav ->
                        when (nav) {
                            is LoginScreenContract.Effect.Navigation.NavigateToHome ->
                                navVm.setEvent(NavEvent.Replace(LoginStep.Success))
                            is LoginScreenContract.Effect.Navigation.NavigateToOtp ->
                                navVm.setEvent(NavEvent.Push(LoginStep.Otp(nav.userId)))
                        }
                    }
                )
            }
            entry<LoginStep.Otp> { otp ->
                OtpScreen(
                    userId = otp.userId,
                    onBack = { navVm.setEvent(NavEvent.Pop()) },
                    onOtpSuccess = { navVm.setEvent(NavEvent.Replace(LoginStep.Success)) },
                )
            }
            entry<LoginStep.Success> {
                LaunchedEffect(Unit) { onSuccess() }
            }
        }
    )
}
