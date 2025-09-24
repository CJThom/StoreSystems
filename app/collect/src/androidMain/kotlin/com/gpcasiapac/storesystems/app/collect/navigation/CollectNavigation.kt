package com.gpcasiapac.storesystems.app.collect.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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

// App-level destinations (Host-only navigation)
@Serializable
sealed interface CollectAppDestination : NavKey {
    @Serializable
    data object LoginHost : CollectAppDestination

    @Serializable
    data object CollectHost : CollectAppDestination
}

/**
 * App-level Navigation3 host that starts at Login Host and navigates to Collect Host on success.
 */
@Composable
fun AndroidAppNavigation(
    appNavigationViewModel: CollectAppNavigationViewModel = koinViewModel(),
) {
    val state by appNavigationViewModel.viewState.collectAsState()

    val loginEntry: LoginFeatureEntry = koinInject()
    val collectEntry: CollectOrdersFeatureEntry = koinInject()

    NavDisplay(
        backStack = state.stack,
        onBack = { count -> appNavigationViewModel.setEvent(CollectAppNavContract.Event.PopBack(count)) },
        entryDecorators = listOf(
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<CollectAppDestination.LoginHost> {
                loginEntry.Host(onComplete = {
                    appNavigationViewModel.setEvent(CollectAppNavContract.Event.ToCollectHost)
                })
            }

            // Entry that renders the Collect feature Host
            entry<CollectAppDestination.CollectHost> {
                collectEntry.Host()
            }
        }
    )
}
