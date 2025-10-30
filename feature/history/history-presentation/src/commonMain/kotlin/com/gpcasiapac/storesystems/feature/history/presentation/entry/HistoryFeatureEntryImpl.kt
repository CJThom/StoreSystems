package com.gpcasiapac.storesystems.feature.history.presentation.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSaveableStateHolderNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.gpcasiapac.storesystems.feature.history.api.HistoryExternalOutcome
import com.gpcasiapac.storesystems.feature.history.api.HistoryFeatureDestination
import com.gpcasiapac.storesystems.feature.history.api.HistoryFeatureEntry
import com.gpcasiapac.storesystems.feature.history.api.HistoryOutcome
import com.gpcasiapac.storesystems.feature.history.presentation.destination.history.HistoryScreenContract
import com.gpcasiapac.storesystems.feature.history.presentation.destination.history.HistoryScreenDestination
import com.gpcasiapac.storesystems.feature.history.presentation.navigation.HistoryNavigationContract
import com.gpcasiapac.storesystems.feature.history.presentation.navigation.HistoryNavigationViewModel
import org.koin.compose.viewmodel.koinViewModel

class HistoryFeatureEntryImpl : HistoryFeatureEntry {

    @Composable
    override fun Host(
        onExternalOutcome: (HistoryExternalOutcome) -> Unit,
    ) {
        val navViewModel: HistoryNavigationViewModel = koinViewModel()
        val state by navViewModel.viewState.collectAsStateWithLifecycle()

        LaunchedEffect(Unit) {
            navViewModel.effect.collect { effect ->
                when (effect) {
                    is HistoryNavigationContract.Effect.ExternalOutcome -> onExternalOutcome(effect.externalOutcome)
                }
            }
        }

        NavDisplay(
            backStack = state.stack,
            onBack = {
                navViewModel.setEvent(HistoryNavigationContract.Event.PopBack(1))
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                registerEntries(
                    builder = this,
                    onOutcome = { outcome ->
                        navViewModel.setEvent(HistoryNavigationContract.Event.Outcome(outcome))
                    }
                )
            }
        )
    }

    override fun registerEntries(
        builder: EntryProviderScope<NavKey>,
        onOutcome: (HistoryOutcome) -> Unit,
    ) {
        builder.apply {
            entry<HistoryFeatureDestination.History> {
                HistoryScreenDestination { outcome ->
                    when (outcome) {
                        is HistoryScreenContract.Effect.Outcome.Back -> onOutcome(HistoryOutcome.Back)
                        is HistoryScreenContract.Effect.Outcome.OpenDetails -> {
                            onOutcome(HistoryOutcome.OpenDetails(outcome.title, outcome.groupKey))
                        }
                    }
                }
            }

            entry<HistoryFeatureDestination.HistoryDetails> { details ->
                val vm: com.gpcasiapac.storesystems.feature.history.presentation.destination.historydetails.HistoryDetailsScreenViewModel = koinViewModel()
                val state by vm.viewState.collectAsStateWithLifecycle()

                LaunchedEffect(details) {
                    val d = details as HistoryFeatureDestination.HistoryDetails
                    vm.setEvent(
                        com.gpcasiapac.storesystems.feature.history.presentation.destination.historydetails.HistoryDetailsScreenContract.Event.Initialize(
                            title = d.title,
                            groupKey = d.groupKey
                        )
                    )
                }

                LaunchedEffect(Unit) {
                    vm.effect.collect { eff ->
                        when (eff) {
                            is com.gpcasiapac.storesystems.feature.history.presentation.destination.historydetails.HistoryDetailsScreenContract.Effect.Outcome.Back ->
                                onOutcome(HistoryOutcome.Back)
                            is com.gpcasiapac.storesystems.feature.history.presentation.destination.historydetails.HistoryDetailsScreenContract.Effect.ShowError -> { /* no-op here */ }
                        }
                    }
                }

                com.gpcasiapac.storesystems.feature.history.presentation.destination.historydetails.HistoryDetailsScreen(
                    state = state,
                    onEvent = vm::setEvent,
                    effectFlow = vm.effect,
                    onOutcome = { eff ->
                        when (eff) {
                            is com.gpcasiapac.storesystems.feature.history.presentation.destination.historydetails.HistoryDetailsScreenContract.Effect.Outcome.Back ->
                                onOutcome(HistoryOutcome.Back)
                        }
                    }
                )
            }
        }
    }
}
