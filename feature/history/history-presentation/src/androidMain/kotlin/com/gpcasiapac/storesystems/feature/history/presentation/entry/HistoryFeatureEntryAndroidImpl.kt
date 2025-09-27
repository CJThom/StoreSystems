package com.gpcasiapac.storesystems.feature.history.presentation.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.gpcasiapac.storesystems.feature.history.api.HistoryExternalOutcome
import com.gpcasiapac.storesystems.feature.history.api.HistoryFeatureDestination
import com.gpcasiapac.storesystems.feature.history.api.HistoryFeatureEntry
import com.gpcasiapac.storesystems.feature.history.api.HistoryOutcome
import com.gpcasiapac.storesystems.feature.history.presentation.destination.history.HistoryScreenContract
import com.gpcasiapac.storesystems.feature.history.presentation.destination.history.HistoryScreenDestination
import com.gpcasiapac.storesystems.feature.history.presentation.navigation.HistoryNavigationContract
import com.gpcasiapac.storesystems.feature.history.presentation.navigation.HistoryNavigationViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

class HistoryFeatureEntryAndroidImpl : HistoryFeatureEntry {

    @Composable
    override fun Host(
        onExternalOutcome: (HistoryExternalOutcome) -> Unit,
    ) {
        val navViewModel: HistoryNavigationViewModel = koinViewModel()
        val historyEntry: HistoryFeatureEntry = koinInject()

        val state by navViewModel.viewState.collectAsState()

        LaunchedEffect(Unit) {
            navViewModel.effect.collect { effect ->
                when (effect) {
                    is HistoryNavigationContract.Effect.ExternalOutcome -> onExternalOutcome(effect.externalOutcome)
                }
            }
        }

        NavDisplay(
            backStack = state.stack,
            onBack = { count ->
                navViewModel.setEvent(HistoryNavigationContract.Event.PopBack(count))
            },
            entryDecorators = listOf(
                rememberSavedStateNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                historyEntry.registerEntries(
                    builder = this,
                    onOutcome = { outcome ->
                        navViewModel.setEvent(HistoryNavigationContract.Event.Outcome(outcome))
                    }
                )
            }
        )
    }

    override fun registerEntries(
        builder: EntryProviderBuilder<NavKey>,
        onOutcome: (HistoryOutcome) -> Unit,
    ) {
        builder.apply {
            entry<HistoryFeatureDestination.History> {
                HistoryScreenDestination { outcome ->
                    when (outcome) {
                        is HistoryScreenContract.Effect.Outcome.Back -> onOutcome(HistoryOutcome.Back)
                    }
                }
            }
        }
    }
}