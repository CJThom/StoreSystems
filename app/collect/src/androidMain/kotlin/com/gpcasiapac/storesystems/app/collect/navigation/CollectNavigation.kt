package com.gpcasiapac.storesystems.app.collect.navigation

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.gpcasiapac.storesystems.feature.collect.api.CollectOrdersFeatureEntry
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureEntry
import kotlinx.serialization.Serializable
import org.koin.compose.koinInject

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
fun AndroidAppNavigation() {
    // Single back stack starting at Login Host
    val backStack = rememberNavBackStack<NavKey>(CollectAppDestination.LoginHost)

    val loginEntry: LoginFeatureEntry = koinInject()
    val collectEntry: CollectOrdersFeatureEntry = koinInject()

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            // Entry that renders the Login feature Host only; no registerEntries used
            entry<CollectAppDestination.LoginHost> {
                loginEntry.Host(onSuccess = { backStack.add(CollectAppDestination.CollectHost) })
            }

            // Entry that renders the Collect feature Host
            entry<CollectAppDestination.CollectHost> {
                collectEntry.Host()
            }
        }
    )
}