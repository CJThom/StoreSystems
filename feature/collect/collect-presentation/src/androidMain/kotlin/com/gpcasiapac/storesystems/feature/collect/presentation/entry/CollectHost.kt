package com.gpcasiapac.storesystems.feature.collect.presentation.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.gpcasiapac.storesystems.feature.collect.api.CollectOrdersFeatureEntry
import com.gpcasiapac.storesystems.feature.collect.api.CollectOutcome
import com.gpcasiapac.storesystems.feature.collect.presentation.navigation.CollectNavContract
import com.gpcasiapac.storesystems.feature.collect.presentation.navigation.CollectNavigationViewModel
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CollectHost(
    collectNavigationViewModel: CollectNavigationViewModel = koinViewModel(),
) {
    val state by collectNavigationViewModel.viewState.collectAsState()

    val collectEntry: CollectOrdersFeatureEntry = koinInject()

    NavDisplay(
        backStack = state.stack,
        onBack = { count -> collectNavigationViewModel.setEvent(CollectNavContract.Event.PopBack(count)) },
        entryDecorators = listOf(
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            // Reuse feature entries; forward outcomes to this feature VM
            collectEntry.registerEntries(
                builder = this,
                onOutcome = { outcome ->
                    when (outcome) {
                        is CollectOutcome.OrderSelected ->
                            collectNavigationViewModel.setEvent(CollectNavContract.Event.Outcome(outcome))
                        is CollectOutcome.Back ->
                            collectNavigationViewModel.setEvent(CollectNavContract.Event.PopBack())
                    }
                }
            )
        }
    )
}
