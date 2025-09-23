package com.gpcasiapac.storesystems.feature.login.presentation.navigation

import com.gpcasiapac.storesystems.common.feature_flags.FeatureFlags
import com.gpcasiapac.storesystems.common.presentation.navigation.BaseNavViewModel
import com.gpcasiapac.storesystems.feature.login.api.LoginFlags
import org.koin.core.component.KoinComponent

class LoginNavViewModel(
    private val flags: FeatureFlags
) : BaseNavViewModel<LoginNavContract.Event, LoginStep>(), KoinComponent {

    override fun provideStartKey(): LoginStep = LoginStep.Login

    override fun handleEvents(event: LoginNavContract.Event) {
        when (event) {
            is LoginNavContract.Event.ProceedToOtp -> proceedToOtp(event)
            LoginNavContract.Event.CompleteAuthentication -> replaceTop(LoginStep.Success)
            is LoginNavContract.Event.PopBack -> pop(event.count)
        }
    }

    private fun proceedToOtp(event: LoginNavContract.Event.ProceedToOtp) {
        // Example policy: honor feature flag at nav layer too
        if (flags.isEnabled(LoginFlags.MfaRequired)) {
            push(LoginStep.Otp(event.userId))
        } else {
            // Skip OTP step entirely and finish the flow
            replaceTop(LoginStep.Success)
        }
    }

}
