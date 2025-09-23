package com.gpcasiapac.storesystems.feature.login.presentation.navigation

import com.gpcasiapac.storesystems.common.presentation.navigation.BaseNavViewModel
import com.gpcasiapac.storesystems.common.presentation.navigation.NavEvent

class LoginNavViewModel : BaseNavViewModel<LoginStep>() {
    override fun provideStartKey(): LoginStep = LoginStep.Login

    // Domain-level events mapping to NavEvent if you choose to expose them later
    fun onSubmitCredentials(userId: String) {
        setEvent(NavEvent.Push(LoginStep.Otp(userId)))
    }

    fun onSubmitOtpSuccess() {
        setEvent(NavEvent.Replace(LoginStep.Success))
    }

    fun onBack() {
        setEvent(NavEvent.Pop())
    }
}
