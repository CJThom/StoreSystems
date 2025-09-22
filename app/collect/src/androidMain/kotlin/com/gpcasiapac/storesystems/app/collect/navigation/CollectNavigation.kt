package com.gpcasiapac.storesystems.app.collect.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureDestination
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureEntry
import org.koin.compose.koinInject

/**
 * Simple Navigation3 host for the Collect app that starts at Login
 * and calls the feature's Host() inside an entry.
 */
@Composable
fun AndroidAppNavigation() {
    // Single back stack starting at Login
    val backStack = rememberNavBackStack<NavKey>(LoginFeatureDestination.Login)

    val loginEntry: LoginFeatureEntry = koinInject()

    NavDisplay(
        backStack = backStack,
        onBack = { backStack.removeLastOrNull() },
        entryDecorators = listOf(
            rememberSceneSetupNavEntryDecorator(),
        ),
        entryProvider = entryProvider {
            // Use Host() directly for the feature entry as requested
            entry<LoginFeatureDestination.Login> {
                loginEntry.Host()
            }
        }
    )
}