package com.gpcasiapac.storesystems.app.superapp.navigation.globalpatternexample

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.gpcasiapac.storesystems.app.superapp.component.TabsNavigationBar
import com.gpcasiapac.storesystems.app.superapp.navigation.HistoryFeatureDestination
import com.gpcasiapac.storesystems.app.superapp.navigation.PickingFeatureDestination
import com.gpcasiapac.storesystems.app.superapp.navigation.TabItem
import com.gpcasiapac.storesystems.feature.collect.api.CollectFeatureEntry
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureEntry
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SuperAppGlobalNavigation() {
    val superNavigationViewModel: SuperGlobalNavigationViewModel = koinViewModel()
    val state by superNavigationViewModel.viewState.collectAsState()

    val loginEntry: LoginFeatureEntry = koinInject()
    val collectEntry: CollectFeatureEntry = koinInject()

    // Outer shell NavDisplay controls Login flow (via registerEntries) -> TabsHost
    NavDisplay(
        backStack = state.appShellStack,
        onBack = {
            superNavigationViewModel.setEvent(SuperGlobalNavContract.Event.Shell.Pop)
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            // Register Login feature entries; VM handles outcomes and pushes keys into appShellStack
            loginEntry.registerEntries(this) { outcome ->
                superNavigationViewModel.setEvent(
                    SuperGlobalNavContract.Event.Shell.FromLogin(
                        outcome
                    )
                )
            }

            // Shell: Tabs host. Inside, use a tiny selector NavDisplay; each tab entry hosts its own NavDisplay
            entry<AppShellKey.TabsHost> {
                Scaffold(
                    bottomBar = {
                        TabsNavigationBar(
                            selected = state.selectedTab,
                            tabList = state.tabList,
                            onSelect = { tab ->
                                superNavigationViewModel.setEvent(
                                    SuperGlobalNavContract.Event.TabsHost.SelectTab(tab)
                                )
                            }
                        )
                    }
                ) { padding ->
                    Box(Modifier.padding(padding)) {
                        // Inner selector: shows exactly one tab entry
                        NavDisplay(
                            backStack = listOf(state.selectedTab),
                            onBack = {
                                superNavigationViewModel.setEvent(
                                    SuperGlobalNavContract.Event.TabsHost.Pop(tab = state.selectedTab)
                                )
                            },
                            entryProvider = entryProvider {
                                // Picking tab entry: its own NavDisplay bound to pickingStack
                                entry<TabItem.Picking> {
                                    NavDisplay(
                                        backStack = state.pickingStack,
                                        onBack = {
                                            superNavigationViewModel.setEvent(
                                                SuperGlobalNavContract.Event.TabsHost.Pop(TabItem.Picking())
                                            )
                                        },
                                        entryDecorators = listOf(
                                            rememberSaveableStateHolderNavEntryDecorator(),
                                            rememberViewModelStoreNavEntryDecorator()
                                        ),
                                        entryProvider = entryProvider {
                                            // TODO: Replace stub with pickingEntry.registerEntries when available
                                            entry<PickingFeatureDestination.Root> { Text("Picking (stub)") }
                                        }
                                    )
                                }

                                // Collect tab entry: its own NavDisplay bound to collectStack
                                entry<TabItem.Collect> {
                                    NavDisplay(
                                        backStack = state.collectStack,
                                        onBack = {
                                            superNavigationViewModel.setEvent(
                                                SuperGlobalNavContract.Event.TabsHost.Pop(TabItem.Collect())
                                            )
                                        },
                                        entryDecorators = listOf(
                                            rememberSaveableStateHolderNavEntryDecorator(),
                                            rememberViewModelStoreNavEntryDecorator()
                                        ),
                                        entryProvider = entryProvider {
                                            collectEntry.registerEntries(this) { outcome ->
                                                superNavigationViewModel.setEvent(
                                                    SuperGlobalNavContract.Event.TabsHost.FromCollect(
                                                        outcome
                                                    )
                                                )
                                            }
                                        }
                                    )
                                }

                                // History tab entry: its own NavDisplay bound to historyStack
                                entry<TabItem.History> {
                                    NavDisplay(
                                        backStack = state.historyStack,
                                        onBack = {
                                            superNavigationViewModel.setEvent(
                                                SuperGlobalNavContract.Event.TabsHost.Pop(TabItem.History())
                                            )
                                        },
                                        entryDecorators = listOf(
                                            rememberSaveableStateHolderNavEntryDecorator(),
                                            rememberViewModelStoreNavEntryDecorator()
                                        ),
                                        entryProvider = entryProvider {
                                            entry<HistoryFeatureDestination.Root> { Text("History (stub)") }
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    )
}
