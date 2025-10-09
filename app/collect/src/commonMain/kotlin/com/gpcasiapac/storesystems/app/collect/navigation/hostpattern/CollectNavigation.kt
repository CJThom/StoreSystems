package com.gpcasiapac.storesystems.app.collect.navigation.hostpattern

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.gpcasiapac.storesystems.feature.collect.api.CollectFeatureEntry
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureEntry
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * App-level Navigation3 host that starts at Login Host and navigates to Collect Host based on outcomes.
 */
@OptIn(ExperimentalMaterial3AdaptiveApi::class)
@Composable
fun AndroidAppNavigation(
    appNavigationViewModel: CollectAppNavigationViewModel = koinViewModel(),
) {
    val state by appNavigationViewModel.viewState.collectAsStateWithLifecycle()
    val loginEntry: LoginFeatureEntry = koinInject()
    val collectEntry: CollectFeatureEntry = koinInject()

    NavDisplay(
        backStack = state.stack,
        onBack = {
            appNavigationViewModel.setEvent(CollectAppNavContract.Event.PopBack(1))
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<CollectAppDestination.LoginHost> {
                loginEntry.Host(
                    onExternalOutcome = { externalOutcome ->
                        appNavigationViewModel.setEvent(
                            CollectAppNavContract.Event.FromLogin(externalOutcome)
                        )
                    },
                )
            }

            entry<CollectAppDestination.CollectHost> {
                collectEntry.Host(
                    onExternalOutcome = { externalOutcome ->
                        appNavigationViewModel.setEvent(
                            CollectAppNavContract.Event.FromCollect(externalOutcome)
                        )
                    },
                )
            }

        }
    )
}
