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
import com.gpcasiapac.storesystems.feature.collect.api.CollectExternalOutcome
import com.gpcasiapac.storesystems.feature.collect.api.CollectFeatureDestination
import com.gpcasiapac.storesystems.feature.collect.api.CollectFeatureEntry
import com.gpcasiapac.storesystems.feature.collect.api.CollectOutcome
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail.OrderDetailScreenDestination
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail.OrderDetailScreenContract
import com.gpcasiapac.storesystems.feature.collect.presentation.navigation.CollectNavigationContract
import com.gpcasiapac.storesystems.feature.collect.presentation.navigation.CollectNavigationViewModel
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.OrderListScreenDestination
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.OrderListScreenContract
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.SignatureScreenDestination
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.SignatureScreenContract
import org.koin.compose.koinInject
import org.koin.compose.viewmodel.koinViewModel

class CollectFeatureEntryAndroidImpl : CollectFeatureEntry {

    @Composable
    override fun Host(
        onExternalOutcome: (CollectExternalOutcome) -> Unit,
    ) {
        val collectNavigationViewModel: CollectNavigationViewModel = koinViewModel()
        val collectEntry: CollectFeatureEntry = koinInject()

        val state by collectNavigationViewModel.viewState.collectAsState()

        LaunchedEffect(Unit) {
            collectNavigationViewModel.effect.collect { effect ->
                when (effect) {
                    is CollectNavigationContract.Effect.ExternalOutcome -> onExternalOutcome(effect.externalOutcome)
                }
            }
        }

        NavDisplay(
            backStack = state.stack,
            onBack = { count ->
                collectNavigationViewModel.setEvent(
                    CollectNavigationContract.Event.PopBack(
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
                        collectNavigationViewModel.setEvent(CollectNavigationContract.Event.Outcome(outcome))
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
                OrderListScreenDestination { outcome ->
                    when (outcome) {
                        is OrderListScreenContract.Effect.Outcome.OrderSelected -> onOutcome(
                            CollectOutcome.OrderSelected(outcome.orderId)
                        )
                    }
                }
            }

            entry<CollectFeatureDestination.OrderDetails> { d ->
                OrderDetailScreenDestination { effect ->
                    when (effect) {
                        is OrderDetailScreenContract.Effect.Outcome.Back -> onOutcome(CollectOutcome.Back)
                    }
                }
            }

            entry<CollectFeatureDestination.Signature> {
                SignatureScreenDestination { outcome ->
                    when (outcome) {
                        is SignatureScreenContract.Effect.Outcome.Back -> onOutcome(CollectOutcome.Back)
                    }
                }
            }
        }
    }
}
