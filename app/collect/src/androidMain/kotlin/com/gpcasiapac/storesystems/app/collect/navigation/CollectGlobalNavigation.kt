package com.gpcasiapac.storesystems.app.collect.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.gpcasiapac.storesystems.app.collect.navigation.globalpattern.CollectGlobalNavContract
import com.gpcasiapac.storesystems.app.collect.navigation.globalpattern.CollectGlobalNavigationViewModel
import com.gpcasiapac.storesystems.feature.collect.api.CollectFeatureEntry
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureEntry
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

/**
 * Global, single-NavDisplay navigation option for the Collect app.
 * This registers Login and Collect feature entries into one host and delegates
 * all stack mutations to CollectGlobalNavigationViewModel.
 */
@Composable
fun AndroidAppNavigationGlobal() {

    val appNavigationViewModel: CollectGlobalNavigationViewModel = koinViewModel()
    val loginEntry: LoginFeatureEntry = koinInject()
    val collectEntry: CollectFeatureEntry = koinInject()

    val state by appNavigationViewModel.viewState.collectAsState()

    NavDisplay(
        backStack = state.stack,
        onBack = {
            appNavigationViewModel.setEvent(CollectGlobalNavContract.Event.PopBack(1))
        },
        entryDecorators = listOf(
            rememberSaveableStateHolderNavEntryDecorator(),
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
