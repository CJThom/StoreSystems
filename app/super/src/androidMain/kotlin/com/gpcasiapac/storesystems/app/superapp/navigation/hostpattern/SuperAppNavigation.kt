package com.gpcasiapac.storesystems.app.superapp.navigation.hostpattern

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.gpcasiapac.storesystems.app.superapp.navigation.TabItem
import com.gpcasiapac.storesystems.app.superapp.navigation.TabsNavigationBar
import com.gpcasiapac.storesystems.feature.collect.api.CollectFeatureEntry
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureEntry
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SuperAppNavigation() {
    // Shell VM using BaseNavViewModel
    val shellViewModel: SuperAppShellViewModel = koinViewModel()
    val shellState by shellViewModel.viewState.collectAsState()

    val loginEntry: LoginFeatureEntry = koinInject()
    val collectEntry: CollectFeatureEntry = koinInject()

    // Outer shell NavDisplay controls LoginHost -> MainHost
    NavDisplay(
        backStack = shellState.stack,
        onBack = { count -> shellViewModel.setEvent(
            SuperAppShellContract.Event.PopBack(count)
        ) },
        entryDecorators = listOf(
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator(),
            rememberSceneSetupNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            // Login host
            entry<SuperAppDestination.LoginHost> {
                loginEntry.Host { externalOutcome ->
                    shellViewModel.setEvent(
                        SuperAppShellContract.Event.FromLogin(externalOutcome)
                    )
                }
            }

            // Main host (tabs)
            entry<SuperAppDestination.MainHost> {
                TabsHostScreen(collectEntry = collectEntry)
            }
        }
    )
}

@Composable
private fun TabsHostScreen(
    collectEntry: CollectFeatureEntry,
) {
    val tabsVM: TabsHostViewModel = koinViewModel()
    val tabsState by tabsVM.viewState.collectAsState()

    Scaffold(
        bottomBar = {
            TabsNavigationBar(
                selected = tabsState.selectedTab,
                tabList = tabsState.tabList,
                onSelect = { tab ->
                    tabsVM.setEvent(
                        TabsHostContract.Event.SelectTab(tab)
                    )
                }
            )
        }
    ) { padding ->
        Box(Modifier.padding(padding)) {
            // Inner selector: mount exactly one tab Host
            NavDisplay(
                backStack = listOf(tabsState.selectedTab),
                onBack = { /* let child Hosts handle back internally */ },
                entryProvider = entryProvider {
                    entry<TabItem.Picking> {
                        // TODO: replace with real Picking host when available
                        Text("Picking (stub)")
                    }
                    entry<TabItem.Collect> {
                        collectEntry.Host(
                            onExternalOutcome = { ext ->
                                tabsVM.setEvent(
                                    TabsHostContract.Event.FromCollect(ext)
                                )
                            }
                        )
                    }
                    entry<TabItem.History> {
                        // TODO: replace with real History host when available
                        Text("History (stub)")
                    }
                }
            )
        }
    }
}
