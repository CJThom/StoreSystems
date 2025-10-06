package com.gpcasiapac.storesystems.app.collect.navigation.globalpattern

import androidx.navigation3.runtime.NavKey
import com.gpcasiapac.storesystems.app.collect.navigation.hostpattern.CollectAppNavContract
import com.gpcasiapac.storesystems.common.presentation.navigation.BaseNavViewModel
import com.gpcasiapac.storesystems.feature.collect.api.CollectFeatureDestination
import com.gpcasiapac.storesystems.feature.collect.api.CollectFeatureDestination.OrderDetails
import com.gpcasiapac.storesystems.feature.collect.api.CollectFeatureDestination.Signature
import com.gpcasiapac.storesystems.feature.collect.api.CollectOutcome
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureDestination
import com.gpcasiapac.storesystems.feature.login.api.LoginOutcome

/**
 * Single back stack ViewModel for the Collect app that directly handles navigation across
 * Login and Collect features. This VM is an alternative to the host-based navigation and is
 * intended to be the main choice going forward.
 */
class CollectGlobalNavigationViewModel :
    BaseNavViewModel<CollectGlobalNavContract.Event,CollectAppNavContract.State, NavKey>() {

    override fun setInitialState(): CollectAppNavContract.State {
        return CollectAppNavContract.State(stack = listOf(LoginFeatureDestination.Login))
    }

    override fun onStart() {

    }

    override fun handleEvents(event: CollectGlobalNavContract.Event) {
        when (event) {
            is CollectGlobalNavContract.Event.Push -> push(event.key)
            is CollectGlobalNavContract.Event.ReplaceTop -> replaceTop(event.key)
            is CollectGlobalNavContract.Event.PopBack -> pop(event.count)

            is CollectGlobalNavContract.Event.FromLogin -> handleLoginOutcome(event.outcome)
            is CollectGlobalNavContract.Event.FromCollect -> handleCollectOutcome(event.outcome)
        }
    }

    private fun handleLoginOutcome(outcome: LoginOutcome) {
        when (outcome) {
            is LoginOutcome.MfaRequired -> {
                push(LoginFeatureDestination.Mfa(outcome.userId))
            }

            is LoginOutcome.LoginCompleted -> {
                replaceTop(CollectFeatureDestination.Orders)
            }

            is LoginOutcome.Back -> pop()
        }
    }

    private fun handleCollectOutcome(outcome: CollectOutcome) {
        when (outcome) {
            is CollectOutcome.OrderSelected -> {
                push(OrderDetails(outcome.orderId))
            }

            is CollectOutcome.Back -> pop()
            is CollectOutcome.Logout -> {
                replaceTop(LoginFeatureDestination.Login)
            }
            is CollectOutcome.SignatureRequested -> {
                push(Signature)
            }

            is CollectOutcome.SignatureSaved -> pop()
        }
    }
}