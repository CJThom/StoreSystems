package com.gpcasiapac.storesystems.app.collect.navigation

import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.togetherWith
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.gpcasiapac.storesystems.feature.collect.api.CollectFeatureEntry
import com.gpcasiapac.storesystems.feature.history.api.HistoryFeatureEntry
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureEntry
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel


@Composable
fun CollectNavDisplay() {
    val appNavigationViewModel: CollectAppNavigationViewModel = koinViewModel()
    val state by appNavigationViewModel.viewState.collectAsStateWithLifecycle()
    val loginEntry: LoginFeatureEntry = koinInject()
    val collectEntry: CollectFeatureEntry = koinInject()
    val historyEntry: HistoryFeatureEntry = koinInject()

    NavDisplay(
        backStack = state.stack,
        onBack = {
            appNavigationViewModel.setEvent(CollectAppNavContract.Event.PopBack)
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        transitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
        popTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
        predictivePopTransitionSpec = { EnterTransition.None togetherWith ExitTransition.None },
        entryProvider = entryProvider {
            entry<CollectAppNavContract.Destination.LoginHost> {
                loginEntry.Host(
                    onExternalOutcome = { externalOutcome ->
                        appNavigationViewModel.setEvent(
                            CollectAppNavContract.Event.FromLogin(externalOutcome)
                        )
                    },
                )
            }

            entry<CollectAppNavContract.Destination.CollectHost> {
                collectEntry.Host(
                    onExternalOutcome = { externalOutcome ->
                        appNavigationViewModel.setEvent(
                            CollectAppNavContract.Event.FromCollect(externalOutcome)
                        )
                    },
                )
            }

            entry<CollectAppNavContract.Destination.HistoryHost> {
                historyEntry.Host(
                    onExternalOutcome = { externalOutcome ->
                        appNavigationViewModel.setEvent(
                            CollectAppNavContract.Event.FromHistory(externalOutcome)
                        )
                    },
                )
            }

        }
    )
}