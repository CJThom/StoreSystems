package com.gpcasiapac.storesystems.feature.login.presentation.navigation

import com.gpcasiapac.storesystems.common.feature_flags.FeatureFlags
import com.gpcasiapac.storesystems.common.presentation.navigation.BaseNavViewModel
import com.gpcasiapac.storesystems.feature.login.api.LoginFlags
import org.koin.core.component.KoinComponent

class LoginNavigationViewModel(
    private val flags: FeatureFlags
) : BaseNavViewModel<LoginNavContract.Event, LoginStep>(), KoinComponent {

    override fun provideStartKey(): LoginStep = LoginStep.Login

    override fun handleEvents(event: LoginNavContract.Event) {
        when (event) {
            is LoginNavContract.Event.MfaRequired -> onMfaRequired(event)
            is LoginNavContract.Event.LoginCompleted -> replaceTop(LoginStep.Complete)
            is LoginNavContract.Event.PopBack -> pop(event.count)
        }
    }

    private fun onMfaRequired(event: LoginNavContract.Event.MfaRequired) {
        if (flags.isEnabled(LoginFlags.Mfa_V2)) {
            push(LoginStep.Mfa_V2(event.userId))
        } else {
            push(LoginStep.Mfa(event.userId))
        }
    }

}
