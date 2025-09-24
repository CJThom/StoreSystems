package com.gpcasiapac.storesystems.app.superapp.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.gpcasiapac.storesystems.feature.collect.api.CollectOrdersFeatureEntry
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureEntry
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SuperAppNavigation() {
    val superNavigationViewModel: SuperGlobalNavigationViewModel = koinViewModel()
    val state by superNavigationViewModel.viewState.collectAsState()

    val loginEntry: LoginFeatureEntry = koinInject()
    val collectEntry: CollectOrdersFeatureEntry = koinInject()

    // Outer shell NavDisplay controls Login flow (via registerEntries) -> TabsHost
    NavDisplay(
        backStack = state.appShellStack,
        onBack = { count -> superNavigationViewModel.setEvent(SuperGlobalNavContract.Event.Shell.Pop(count)) },
        entryDecorators = listOf(
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            // Register Login feature entries; VM handles outcomes and pushes keys into appShellStack
            loginEntry.registerEntries(this) { outcome ->
                superNavigationViewModel.setEvent(SuperGlobalNavContract.Event.Shell.FromLogin(outcome))
            }

            // Shell: Tabs host. Inside, use a tiny selector NavDisplay; each tab entry hosts its own NavDisplay
            entry<AppShellKey.TabsHost> {
                val selected = state.selectedTab ?: TabHostKey.Collect
                Scaffold(
                    bottomBar = {
                        NavigationBar {
                            NavigationBarItem(
                                selected = selected == TabHostKey.Picking,
                                onClick = { superNavigationViewModel.setEvent(SuperGlobalNavContract.Event.TabsHost.SelectTab(TabHostKey.Picking)) },
                                icon = { },
                                label = { Text("Picking") }
                            )
                            NavigationBarItem(
                                selected = selected == TabHostKey.Collect,
                                onClick = { superNavigationViewModel.setEvent(SuperGlobalNavContract.Event.TabsHost.SelectTab(TabHostKey.Collect)) },
                                icon = { },
                                label = { Text("Collect") }
                            )
                            NavigationBarItem(
                                selected = selected == TabHostKey.History,
                                onClick = { superNavigationViewModel.setEvent(SuperGlobalNavContract.Event.TabsHost.SelectTab(TabHostKey.History)) },
                                icon = { },
                                label = { Text("History") }
                            )
                        }
                    }
                ) { padding ->
                    val tabSelectorBackStack = listOf(selected)

                    Box(Modifier.padding(padding)) {
                        // Inner selector: shows exactly one tab entry
                        NavDisplay(
                            backStack = tabSelectorBackStack,
                            onBack = { count ->
                                superNavigationViewModel.setEvent(
                                    SuperGlobalNavContract.Event.TabsHost.Pop(
                                        selected,
                                        count
                                    )
                                )
                            },
                            entryProvider = entryProvider {
                                // Picking tab entry: its own NavDisplay bound to pickingStack
                                entry<TabHostKey.Picking> {
                                    NavDisplay(
                                        backStack = state.pickingStack,
                                        onBack = { count ->
                                            superNavigationViewModel.setEvent(
                                                SuperGlobalNavContract.Event.TabsHost.Pop(
                                                    TabHostKey.Picking,
                                                    count
                                                )
                                            )
                                        },
                                        entryDecorators = listOf(
                                            rememberSavedStateNavEntryDecorator(),
                                            rememberViewModelStoreNavEntryDecorator(),
                                            rememberSceneSetupNavEntryDecorator()
                                        ),
                                        entryProvider = entryProvider {
                                            // TODO: Replace stub with pickingEntry.registerEntries when available
                                            entry<PickingFeatureDestination.Root> { Text("Picking (stub)") }
                                        }
                                    )
                                }

                                // Collect tab entry: its own NavDisplay bound to collectStack
                                entry<TabHostKey.Collect> {
                                    NavDisplay(
                                        backStack = state.collectStack,
                                        onBack = { count ->
                                            superNavigationViewModel.setEvent(
                                                SuperGlobalNavContract.Event.TabsHost.Pop(
                                                    TabHostKey.Collect,
                                                    count
                                                )
                                            )
                                        },
                                        entryDecorators = listOf(
                                            rememberSavedStateNavEntryDecorator(),
                                            rememberViewModelStoreNavEntryDecorator(),
                                            rememberSceneSetupNavEntryDecorator()
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
                                entry<TabHostKey.History> {
                                    NavDisplay(
                                        backStack = state.historyStack,
                                        onBack = { count ->
                                            superNavigationViewModel.setEvent(
                                                SuperGlobalNavContract.Event.TabsHost.Pop(
                                                    TabHostKey.History,
                                                    count
                                                )
                                            )
                                        },
                                        entryDecorators = listOf(
                                            rememberSavedStateNavEntryDecorator(),
                                            rememberViewModelStoreNavEntryDecorator(),
                                            rememberSceneSetupNavEntryDecorator()
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
