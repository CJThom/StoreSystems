package com.gpcasiapac.storesystems.app.superapp.navigation.globalpatternexample

import androidx.navigation3.runtime.NavKey
import com.gpcasiapac.storesystems.app.superapp.navigation.HistoryFeatureDestination
import com.gpcasiapac.storesystems.app.superapp.navigation.PickingFeatureDestination
import com.gpcasiapac.storesystems.app.superapp.navigation.TabItem
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
            tabList = listOf(TabItem.Picking(), TabItem.Collect(), TabItem.History()),
            selectedTab = TabItem.Picking(),
        )

    override fun onStart() { /* no-op */
    }

    override fun handleEvents(event: SuperGlobalNavContract.Event) {
        when (event) {
            // Shell domain
            is SuperGlobalNavContract.Event.Shell -> when (event) {
                is SuperGlobalNavContract.Event.Shell.FromLogin -> handleLogin(event.outcome)
                is SuperGlobalNavContract.Event.Shell.Pop -> popBack()
            }

            // TabsHost domain
            is SuperGlobalNavContract.Event.TabsHost -> when (event) {
                is SuperGlobalNavContract.Event.TabsHost.SelectTab -> selectTab(event.tab)
                is SuperGlobalNavContract.Event.TabsHost.FromCollect -> handleCollect(event.outcome)
                is SuperGlobalNavContract.Event.TabsHost.Pop -> popBackInTab(event.tab)
            }

        }
    }

    // --- Shell ---
    private fun enterTabsHost() {
        setState {
            copy(
                appShellStack = listOf(AppShellKey.TabsHost), // TODO: Maybe dont reset backstack (ie. remove Login items)
                selectedTab = TabItem.Picking()
            )
        }
    }

    private fun handleLogin(outcome: LoginOutcome) {
        when (outcome) {
            is LoginOutcome.MfaRequired -> pushInShell(LoginFeatureDestination.Mfa(outcome.userId))
            is LoginOutcome.LoginCompleted -> enterTabsHost()
            is LoginOutcome.Back -> popBack()
        }
    }

    private fun popBack() {
        setState {
            val atTabs = appShellStack.lastOrNull() == AppShellKey.TabsHost
            if (!atTabs) {
                copy(appShellStack = BackStackReducer.pop(appShellStack))
            } else {
                when (selectedTab) {
                    is TabItem.Picking -> copy(
                        pickingStack = BackStackReducer.pop(pickingStack)
                    )

                    is TabItem.Collect -> copy(
                        collectStack = BackStackReducer.pop(collectStack)
                    )

                    is TabItem.History -> copy(
                        historyStack = BackStackReducer.pop(historyStack)
                    )

                    null -> this
                }
            }
        }
    }

    private fun selectTab(tab: TabItem) {
        setState { copy(selectedTab = tab) }
    }

    // --- Tabs/Features ---
    private fun handleCollect(outcome: CollectOutcome) {
        when (outcome) {
            is CollectOutcome.OrderSelected -> pushInTab(
                TabItem.Collect(),
                CollectFeatureDestination.OrderFulfilment
            )

            is CollectOutcome.Back -> popBackInTab(TabItem.Collect(), 1)
            CollectOutcome.SignatureRequested -> TODO()
            is CollectOutcome.SignatureSaved -> TODO()
            CollectOutcome.Logout -> TODO()
            is CollectOutcome.NavigateToOrderDetails -> TODO()
        }
    }

    // --- Reducers ---
    private fun pushInShell(key: NavKey) =
        setState { copy(appShellStack = BackStackReducer.push(appShellStack, key)) }

    private fun pushInTab(tab: TabItem, key: NavKey) =
        setState {
            when (tab) {
                is TabItem.Picking -> copy(pickingStack = BackStackReducer.push(pickingStack, key))
                is TabItem.Collect -> copy(collectStack = BackStackReducer.push(collectStack, key))
                is TabItem.History -> copy(historyStack = BackStackReducer.push(historyStack, key))
            }
        }

    private fun popBackInTab(tab: TabItem, count: Int = 1) =
        setState {
            when (tab) {
                is TabItem.Picking -> copy(pickingStack = BackStackReducer.pop(pickingStack, count))
                is TabItem.Collect -> copy(collectStack = BackStackReducer.pop(collectStack, count))
                is TabItem.History -> copy(historyStack = BackStackReducer.pop(historyStack, count))
            }
        }
}