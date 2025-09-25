package com.gpcasiapac.storesystems.feature.collect.presentation.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.gpcasiapac.storesystems.feature.collect.api.CollectFeatureDestination
import com.gpcasiapac.storesystems.feature.collect.api.CollectOrdersFeatureEntry
import com.gpcasiapac.storesystems.feature.collect.api.CollectOutcome
import com.gpcasiapac.storesystems.feature.collect.presentation.details.OrderDetailsScreen
import com.gpcasiapac.storesystems.feature.collect.presentation.navigation.CollectNavContract
import com.gpcasiapac.storesystems.feature.collect.presentation.navigation.CollectNavigationViewModel
import com.gpcasiapac.storesystems.feature.collect.presentation.orders.OrdersDestination
import com.gpcasiapac.storesystems.feature.collect.presentation.orders.OrdersScreenContract
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

class CollectOrdersFeatureEntryAndroidImpl : CollectOrdersFeatureEntry {

    @Composable
    override fun Host(
        onExternalOutcome: (com.gpcasiapac.storesystems.feature.collect.api.CollectExternalOutcome) -> Unit,
    ) {
        val collectNavigationViewModel: CollectNavigationViewModel = koinViewModel()
        val collectEntry: CollectOrdersFeatureEntry = koinInject()

        val state by collectNavigationViewModel.viewState.collectAsState()

        LaunchedEffect(Unit) {
            collectNavigationViewModel.effect.collect { effect ->
                when (effect) {
                    is CollectNavContract.Effect.ExternalOutcome -> onExternalOutcome(effect.outcome)
                }
            }
        }

        NavDisplay(
            backStack = state.stack,
            onBack = { count ->
                collectNavigationViewModel.setEvent(
                    CollectNavContract.Event.PopBack(
                        count
                    )
                )
            },
            entryDecorators = listOf(
                rememberSavedStateNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                // Reuse feature entries; forward outcomes to this feature VM
                collectEntry.registerEntries(
                    builder = this,
                    onOutcome = { outcome ->
                        collectNavigationViewModel.setEvent(CollectNavContract.Event.Outcome(outcome))
                    }
                )
            }
        )
    }

    override fun registerEntries(
        builder: EntryProviderBuilder<NavKey>,
        onOutcome: (CollectOutcome) -> Unit,
    ) {
        builder.apply {
            entry<CollectFeatureDestination.Orders> {
                OrdersDestination { navigationEffect ->
                    when (navigationEffect) {
                        is OrdersScreenContract.Effect.Navigation.OrderSelected -> onOutcome(
                            CollectOutcome.OrderSelected(navigationEffect.orderId)
                        )
                    }
                }
            }

            entry<CollectFeatureDestination.OrderDetails> { d ->
                OrderDetailsScreen(
                    orderId = d.orderId,
                    onBack = { onOutcome(CollectOutcome.Back) },
                )
            }
        }
    }
}
