package com.gpcasiapac.storesystems.app.superapp.navigation.hostpattern

import com.gpcasiapac.storesystems.common.presentation.navigation.BaseNavViewModel
import com.gpcasiapac.storesystems.feature.login.api.LoginExternalOutcome

class SuperAppShellViewModel :
    BaseNavViewModel<SuperAppShellContract.Event,SuperAppShellContract.State, SuperAppDestination>() {

    override fun setInitialState(): SuperAppShellContract.State {
        return SuperAppShellContract.State(stack = listOf(SuperAppDestination.LoginHost))
    }

    override fun onStart() {

    }

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