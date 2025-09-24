package com.gpcasiapac.storesystems.app.collect.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.gpcasiapac.storesystems.feature.collect.api.CollectOrdersFeatureEntry
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureEntry
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Global, single-NavDisplay navigation option for the Collect app.
 * This registers Login and Collect feature entries into one host and delegates
 * all stack mutations to CollectGlobalNavigationViewModel.
 */
@Composable
fun AndroidAppNavigationGlobal(
    appNavigationViewModel: CollectGlobalNavigationViewModel = koinViewModel(),
) {
    val state by appNavigationViewModel.viewState.collectAsState()

    val loginEntry: LoginFeatureEntry = koinInject()
    val collectEntry: CollectOrdersFeatureEntry = koinInject()

    NavDisplay(
        backStack = state.stack,
        onBack = { count -> appNavigationViewModel.setEvent(CollectGlobalNavContract.Event.PopBack(count)) },
        entryDecorators = listOf(
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            // Register Login feature entries and forward outcomes to the global VM
            loginEntry.registerEntries(
                builder = this,
                onOutcome = { outcome ->
                    appNavigationViewModel.setEvent(
                        CollectGlobalNavContract.Event.FromLogin(outcome)
                    )
                }
            )

            // Register Collect feature entries and forward outcomes to the global VM
            collectEntry.registerEntries(
                builder = this,
                onOutcome = { outcome ->
                    appNavigationViewModel.setEvent(
                        CollectGlobalNavContract.Event.FromCollect(outcome)
                    )
                }
            )
        }
    )
}
