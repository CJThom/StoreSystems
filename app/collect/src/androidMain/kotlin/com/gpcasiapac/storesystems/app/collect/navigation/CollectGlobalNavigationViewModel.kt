package com.gpcasiapac.storesystems.app.collect.navigation

import androidx.navigation3.runtime.NavKey
import com.gpcasiapac.storesystems.common.presentation.navigation.BaseNavViewModel
import com.gpcasiapac.storesystems.feature.collect.api.CollectFeatureDestination
import com.gpcasiapac.storesystems.feature.collect.presentation.navigation.CollectNavContract
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureDestination
import com.gpcasiapac.storesystems.feature.login.presentation.navigation.LoginNavContract

/**
 * Single back stack ViewModel for the Collect app that directly handles navigation across
 * Login and Collect features. This VM is an alternative to the host-based navigation and is
 * intended to be the main choice going forward.
 */
class CollectGlobalNavigationViewModel :
    BaseNavViewModel<CollectGlobalNavContract.Event, NavKey>() {

    override fun provideStartKey(): NavKey = LoginFeatureDestination.Login

    override fun handleEvents(event: CollectGlobalNavContract.Event) {
        when (event) {
            is CollectGlobalNavContract.Event.Push -> push(event.key)
            is CollectGlobalNavContract.Event.ReplaceTop -> replaceTop(event.key)
            is CollectGlobalNavContract.Event.PopBack -> pop(event.count)

            is CollectGlobalNavContract.Event.FromLogin -> handleLoginOutcome(event.outcome)
            is CollectGlobalNavContract.Event.FromCollect -> handleCollectOutcome(event.outcome)
        }
    }

    private fun handleLoginOutcome(outcome: com.gpcasiapac.storesystems.feature.login.api.LoginOutcome) {
        when (outcome) {
            is com.gpcasiapac.storesystems.feature.login.api.LoginOutcome.MfaRequired ->
                push(LoginFeatureDestination.Otp(outcome.userId))
            is com.gpcasiapac.storesystems.feature.login.api.LoginOutcome.LoginCompleted ->
                replaceTop(CollectFeatureDestination.Orders)
            is com.gpcasiapac.storesystems.feature.login.api.LoginOutcome.Back -> pop()
        }
    }

    private fun handleCollectOutcome(outcome: com.gpcasiapac.storesystems.feature.collect.api.CollectOutcome) {
        when (outcome) {
            is com.gpcasiapac.storesystems.feature.collect.api.CollectOutcome.OrderSelected ->
                push(CollectFeatureDestination.OrderDetails(outcome.orderId))
            is com.gpcasiapac.storesystems.feature.collect.api.CollectOutcome.Back -> pop()
        }
    }
}
