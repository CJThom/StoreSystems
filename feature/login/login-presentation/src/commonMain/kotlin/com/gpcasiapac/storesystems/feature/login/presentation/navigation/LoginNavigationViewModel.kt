package com.gpcasiapac.storesystems.feature.login.presentation.navigation

import com.gpcasiapac.storesystems.common.feature_flags.FeatureFlags
import com.gpcasiapac.storesystems.common.presentation.navigation.BaseNavViewModel
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureDestination
import com.gpcasiapac.storesystems.feature.login.api.LoginFlags
import com.gpcasiapac.storesystems.feature.login.api.LoginOutcome
import com.gpcasiapac.storesystems.feature.login.api.LoginExternalOutcome
import org.koin.core.component.KoinComponent

class LoginNavigationViewModel(
    private val flags: FeatureFlags
) : BaseNavViewModel<LoginNavContract.Event, LoginFeatureDestination>(), KoinComponent {

    override fun provideStartKey(): LoginFeatureDestination = LoginFeatureDestination.Login

    override fun handleEvents(event: LoginNavContract.Event) {
        when (event) {
            is LoginNavContract.Event.Outcome -> handleOutcome(event.outcome)
            is LoginNavContract.Event.PopBack -> pop(event.count)
        }
    }

    private fun handleOutcome(outcome: LoginOutcome) {
        // Drive internal navigation changes first
        when (outcome) {
            is LoginOutcome.MfaRequired -> onMfaRequired(outcome.userId)
            is LoginOutcome.LoginCompleted -> {
                setEffect {
                    LoginNavContract.Effect.ExternalOutcome(LoginExternalOutcome.LoginCompleted)
                }
            }

            is LoginOutcome.Back -> pop()
        }
    }

    private fun onMfaRequired(userId: String) {
        if (flags.isEnabled(LoginFlags.Mfa_V2)) {
            push(LoginFeatureDestination.Otp(userId))
        } else {
            push(LoginFeatureDestination.Otp(userId))
        }
    }
}
