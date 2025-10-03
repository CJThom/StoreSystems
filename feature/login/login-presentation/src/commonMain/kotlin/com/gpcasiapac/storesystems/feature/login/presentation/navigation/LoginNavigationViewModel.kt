package com.gpcasiapac.storesystems.feature.login.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.gpcasiapac.storesystems.common.feature_flags.FeatureFlags
import com.gpcasiapac.storesystems.common.presentation.navigation.BaseNavViewModel

import com.gpcasiapac.storesystems.feature.login.api.LoginExternalOutcome
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureDestination
import com.gpcasiapac.storesystems.feature.login.api.LoginFlags
import com.gpcasiapac.storesystems.feature.login.api.LoginOutcome
import org.koin.core.component.KoinComponent

class LoginNavigationViewModel(
    private val flags: FeatureFlags
) : BaseNavViewModel<LoginNavigationContract.Event, LoginFeatureDestination, LoginNavigationContract.State>(), KoinComponent {

    override fun provideStartKey(): LoginFeatureDestination = LoginFeatureDestination.Login
    
    override fun createStateWithStack(stack: List<NavKey>): LoginNavigationContract.State = 
        LoginNavigationContract.State(stack = stack)

    override fun handleEvents(event: LoginNavigationContract.Event) {
        when (event) {
            is LoginNavigationContract.Event.Outcome -> handleOutcome(event.outcome)
            is LoginNavigationContract.Event.PopBack -> pop(event.count)
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
        if (flags.isEnabled(LoginFlags.Mfa_V2)) {
            push(LoginFeatureDestination.Mfa(userId))
        } else {
            push(LoginFeatureDestination.Mfa(userId))
        }
    }
}