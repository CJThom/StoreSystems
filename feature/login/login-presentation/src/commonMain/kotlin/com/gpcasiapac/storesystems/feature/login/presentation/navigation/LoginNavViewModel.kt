package com.gpcasiapac.storesystems.feature.login.presentation.navigation

import com.gpcasiapac.storesystems.common.presentation.navigation.BaseNavViewModel

class LoginNavViewModel : BaseNavViewModel<LoginNavContract.Event, LoginStep>() {
    override fun provideStartKey(): LoginStep = LoginStep.Login

    override fun handleEvents(event: LoginNavContract.Event) {
        when (event) {
            is LoginNavContract.Event.SubmitCredentials -> push(LoginStep.Otp(event.userId))
            LoginNavContract.Event.SubmitOtpSuccess -> replaceTop(LoginStep.Success)
            is LoginNavContract.Event.Pop -> pop(event.count)
        }
    }
}
