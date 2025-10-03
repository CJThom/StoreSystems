package com.gpcasiapac.storesystems.app.superapp.navigation.hostpattern

import androidx.navigation3.runtime.NavKey
import com.gpcasiapac.storesystems.common.presentation.navigation.BaseNavViewModel
import com.gpcasiapac.storesystems.common.presentation.navigation.RootNavState
import com.gpcasiapac.storesystems.feature.login.api.LoginExternalOutcome

class SuperAppShellViewModel : BaseNavViewModel<SuperAppShellContract.Event, SuperAppDestination, RootNavState>() {

    override fun provideStartKey(): SuperAppDestination = SuperAppDestination.LoginHost
    
    override fun createStateWithStack(stack: List<NavKey>): RootNavState = 
        RootNavState(stack = stack)

    override fun handleEvents(event: SuperAppShellContract.Event) {
        when (event) {
            is SuperAppShellContract.Event.FromLogin -> handleLoginExternal(event.outcome)
            is SuperAppShellContract.Event.PopBack -> pop(event.count)
        }
    }

    private fun handleLoginExternal(outcome: LoginExternalOutcome) {
        when (outcome) {
            is LoginExternalOutcome.LoginCompleted -> replaceTop(SuperAppDestination.MainHost)
        }
    }
}