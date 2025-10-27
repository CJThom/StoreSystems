package com.gpcasiapac.storesystems.feature.login.presentation.navigation

import com.gpcasiapac.storesystems.common.presentation.navigation.BaseNavViewModel
import com.gpcasiapac.storesystems.feature.login.api.LoginExternalOutcome
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureDestination
import com.gpcasiapac.storesystems.feature.login.api.LoginOutcome
import com.gpcasiapac.storesystems.feature.login.domain.usecase.CheckMfaRequirementUseCase
import org.koin.core.component.KoinComponent

class LoginNavigationViewModel(
    private val checkMfaRequirementUseCase: CheckMfaRequirementUseCase
) : BaseNavViewModel<LoginNavigationContract.Event, LoginNavigationContract.State, LoginFeatureDestination>(),
    KoinComponent {

    override fun setInitialState(): LoginNavigationContract.State {
        return LoginNavigationContract.State(
            stack = listOf(
                LoginFeatureDestination.Login
            )
        )
    }

    override fun onStart() {

    }

    override fun handleEvents(event: LoginNavigationContract.Event) {
        when (event) {
            is LoginNavigationContract.Event.Outcome -> handleOutcome(event.outcome)
            is LoginNavigationContract.Event.PopBack -> pop()
        }
    }

    private fun handleOutcome(outcome: LoginOutcome) {
        // Drive internal navigation changes first
        when (outcome) {
            is LoginOutcome.MfaRequired -> onMfaRequired(outcome.userId)
            is LoginOutcome.LoginCompleted -> {
                setEffect {
                    LoginNavigationContract.Effect.ExternalOutcome(LoginExternalOutcome.LoginCompleted)
                }
            }

            is LoginOutcome.Back -> pop()
        }
    }

    private fun onMfaRequired(userId: String) {
        if (checkMfaRequirementUseCase()) {
            push(LoginFeatureDestination.Mfa(userId))
        } else {
            push(LoginFeatureDestination.Mfa(userId))
        }
    }
}