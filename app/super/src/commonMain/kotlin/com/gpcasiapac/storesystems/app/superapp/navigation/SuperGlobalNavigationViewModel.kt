package com.gpcasiapac.storesystems.app.superapp.navigation

import androidx.navigation3.runtime.NavKey
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect
import com.gpcasiapac.storesystems.common.presentation.navigation.BackStackReducer
import com.gpcasiapac.storesystems.feature.collect.api.CollectFeatureDestination
import com.gpcasiapac.storesystems.feature.collect.api.CollectOutcome
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureDestination
import com.gpcasiapac.storesystems.feature.login.api.LoginOutcome

class SuperGlobalNavigationViewModel :
    MVIViewModel<SuperGlobalNavContract.Event, SuperGlobalNavContract.State, ViewSideEffect>() {

    override fun setInitialState(): SuperGlobalNavContract.State =
        SuperGlobalNavContract.State(
            appShellStack = listOf(LoginFeatureDestination.Login),
            pickingStack = listOf(PickingFeatureDestination.Root),
            collectStack = listOf(CollectFeatureDestination.Orders),
            historyStack = listOf(HistoryFeatureDestination.Root),
            selectedTab = null,
        )

    override fun onStart() { /* no-op */ }

    override fun handleEvents(event: SuperGlobalNavContract.Event) {
        when (event) {
            // Shell domain
            is SuperGlobalNavContract.Event.Shell -> when (event) {
                is SuperGlobalNavContract.Event.Shell.FromLogin -> handleLogin(event.outcome)
                is SuperGlobalNavContract.Event.Shell.Pop -> popBack(event.count)
            }

            // TabsHost domain
            is SuperGlobalNavContract.Event.TabsHost -> when (event) {
                is SuperGlobalNavContract.Event.TabsHost.SelectTab -> selectTab(event.tab)
                is SuperGlobalNavContract.Event.TabsHost.FromCollect -> handleCollect(event.outcome)
                is SuperGlobalNavContract.Event.TabsHost.Pop -> popBackInTab(event.tab, event.count)
            }

        }
    }

    // --- Shell ---
    private fun enterTabsHost() {
        setState { copy(appShellStack = listOf(AppShellKey.TabsHost), selectedTab = TabHostKey.Collect) }
    }

    private fun handleLogin(outcome: LoginOutcome) {
        when (outcome) {
            is LoginOutcome.MfaRequired -> pushInShell(LoginFeatureDestination.Otp(outcome.userId))
            is LoginOutcome.LoginCompleted -> enterTabsHost()
            is LoginOutcome.Back -> popBack(1)
        }
    }

    private fun popBack(count: Int) {
        setState {
            val atTabs = appShellStack.lastOrNull() == AppShellKey.TabsHost && selectedTab != null
            if (!atTabs) {
                copy(appShellStack = BackStackReducer.pop(appShellStack, count))
            } else {
                when (selectedTab) {
                    TabHostKey.Picking -> copy(pickingStack = BackStackReducer.pop(pickingStack, count))
                    TabHostKey.Collect -> copy(collectStack = BackStackReducer.pop(collectStack, count))
                    TabHostKey.History -> copy(historyStack = BackStackReducer.pop(historyStack, count))
                    null -> this
                }
            }
        }
    }

    private fun selectTab(tab: TabHostKey) {
        setState { copy(selectedTab = tab) }
    }

    // --- Tabs/Features ---
    private fun handleCollect(outcome: CollectOutcome) {
        when (outcome) {
            is CollectOutcome.OrderSelected -> pushInTab(TabHostKey.Collect, CollectFeatureDestination.OrderDetails(outcome.orderId))
            is CollectOutcome.Back -> popBackInTab(TabHostKey.Collect, 1)
        }
    }

    // --- Reducers ---
    private fun pushInShell(key: NavKey) =
        setState { copy(appShellStack = BackStackReducer.push(appShellStack, key)) }

    private fun pushInTab(tab: TabHostKey, key: NavKey) =
        setState {
            when (tab) {
                TabHostKey.Picking -> copy(pickingStack = BackStackReducer.push(pickingStack, key))
                TabHostKey.Collect -> copy(collectStack = BackStackReducer.push(collectStack, key))
                TabHostKey.History -> copy(historyStack = BackStackReducer.push(historyStack, key))
            }
        }

    private fun popBackInTab(tab: TabHostKey, count: Int = 1) =
        setState {
            when (tab) {
                TabHostKey.Picking -> copy(pickingStack = BackStackReducer.pop(pickingStack, count))
                TabHostKey.Collect -> copy(collectStack = BackStackReducer.pop(collectStack, count))
                TabHostKey.History -> copy(historyStack = BackStackReducer.pop(historyStack, count))
            }
        }
}