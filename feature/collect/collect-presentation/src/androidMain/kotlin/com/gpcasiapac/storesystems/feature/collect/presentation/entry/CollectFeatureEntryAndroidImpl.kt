package com.gpcasiapac.storesystems.feature.collect.presentation.entry

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.scene.rememberSceneSetupNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.gpcasiapac.storesystems.feature.collect.api.CollectExternalOutcome
import com.gpcasiapac.storesystems.feature.collect.api.CollectFeatureDestination
import com.gpcasiapac.storesystems.feature.collect.api.CollectFeatureEntry
import com.gpcasiapac.storesystems.feature.collect.api.CollectOutcome
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.OrderFulfilmentScreenContract
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.OrderFulfilmentScreenDestination
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.OrderListScreenContract
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.OrderListScreenDestination
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.SignatureScreenContract
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.SignatureScreenDestination
import com.gpcasiapac.storesystems.feature.collect.presentation.navigation.CollectNavigationContract
import com.gpcasiapac.storesystems.feature.collect.presentation.navigation.CollectNavigationViewModel
import org.koin.compose.viewmodel.koinViewModel

class CollectFeatureEntryAndroidImpl : CollectFeatureEntry {

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    @Composable
    override fun Host(
        onExternalOutcome: (CollectExternalOutcome) -> Unit,
    ) {
        val collectNavigationViewModel: CollectNavigationViewModel = koinViewModel()
        val sceneStrategy = rememberListDetailSceneStrategy<NavKey>()

        val state by collectNavigationViewModel.viewState.collectAsState()

        LaunchedEffect(Unit) {
            collectNavigationViewModel.effect.collect { effect ->
                when (effect) {
                    is CollectNavigationContract.Effect.ExternalOutcome -> onExternalOutcome(effect.externalOutcome)
                }
            }
        }

        NavDisplay(
            sceneStrategy = sceneStrategy,
            backStack = state.stack,
            onBack = { count ->
                collectNavigationViewModel.setEvent(
                    CollectNavigationContract.Event.PopBack(
                        count
                    )
                )
            },
            entryDecorators = listOf(
                rememberSceneSetupNavEntryDecorator(),
                rememberSavedStateNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                // Reuse feature entries; forward outcomes to this feature VM
                registerEntries(
                    builder = this,
                    onOutcome = { outcome ->
                        collectNavigationViewModel.setEvent(CollectNavigationContract.Event.Outcome(outcome))
                    }
                )
            }
        )
    }

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    override fun registerEntries(
        builder: EntryProviderBuilder<NavKey>,
        onOutcome: (CollectOutcome) -> Unit,
    ) {
        builder.apply {
            entry<CollectFeatureDestination.Orders>(
                metadata = ListDetailSceneStrategy.listPane(),
                key = CollectFeatureDestination.Orders
            ) {
                OrderListScreenDestination { outcome ->
                    when (outcome) {
                        is OrderListScreenContract.Effect.Outcome.OrderSelected -> onOutcome(
                            CollectOutcome.OrderSelected(outcome.orderId)
                        )

                        is OrderListScreenContract.Effect.Outcome.OrdersSelected -> {
                            // For now, just navigate to the first selected order or ignore
                            outcome.orderIds.firstOrNull()?.let { id ->
                                onOutcome(CollectOutcome.OrderSelected(id))
                            } ?: run {
                                // No selection; no-op
                            }
                        }

                        is OrderListScreenContract.Effect.Outcome.Back -> onOutcome(CollectOutcome.Back)
                        is OrderListScreenContract.Effect.Outcome.Logout -> onOutcome(CollectOutcome.Logout)
                    }
                }
            }

            entry<CollectFeatureDestination.OrderFulfilment>(
                metadata = ListDetailSceneStrategy.detailPane(),
                key = CollectFeatureDestination.OrderFulfilment
            ) { d ->
                OrderFulfilmentScreenDestination { effect ->
                    when (effect) {
                        is OrderFulfilmentScreenContract.Effect.Outcome.Back -> onOutcome(CollectOutcome.Back)
                        is OrderFulfilmentScreenContract.Effect.Outcome.Confirmed -> onOutcome(
                            CollectOutcome.Back
                        ) // TODO: Swap for Submit?
                        is OrderFulfilmentScreenContract.Effect.Outcome.SignatureRequested -> onOutcome(
                            CollectOutcome.SignatureRequested
                        )
                    }
                }
            }

            entry<CollectFeatureDestination.Signature>(
                metadata = ListDetailSceneStrategy.extraPane(),
                key = CollectFeatureDestination.Signature
            ) {
                SignatureScreenDestination { outcome ->
                    when (outcome) {
                        is SignatureScreenContract.Effect.Outcome.Back -> onOutcome(CollectOutcome.Back)
                        is SignatureScreenContract.Effect.Outcome.SignatureSaved -> {
                            // TODO: Pass signature data to order detail screen
                            onOutcome(CollectOutcome.Back)
                        }
                    }
                }
            }
        }
    }
}
