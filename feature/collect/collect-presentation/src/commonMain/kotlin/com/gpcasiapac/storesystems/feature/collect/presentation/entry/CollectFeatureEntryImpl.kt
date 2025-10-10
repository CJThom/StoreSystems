package com.gpcasiapac.storesystems.feature.collect.presentation.entry

import androidx.compose.material3.adaptive.ExperimentalMaterial3AdaptiveApi
import androidx.compose.material3.adaptive.navigation3.ListDetailSceneStrategy
import androidx.compose.material3.adaptive.navigation3.rememberListDetailSceneStrategy
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
import com.gpcasiapac.storesystems.feature.collect.api.CollectExternalOutcome
import com.gpcasiapac.storesystems.feature.collect.api.CollectFeatureDestination
import com.gpcasiapac.storesystems.feature.collect.api.CollectFeatureEntry
import com.gpcasiapac.storesystems.feature.collect.api.CollectOutcome
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetails.OrderDetailsScreenContract
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetails.OrderDetailsScreenDestination
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.OrderFulfilmentScreenContract
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.OrderFulfilmentScreenDestination
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.OrderListScreenContract
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.OrderListScreenDestination
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.SignatureScreenContract
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.signature.SignatureScreenDestination
import com.gpcasiapac.storesystems.feature.collect.presentation.navigation.CollectNavigationContract
import com.gpcasiapac.storesystems.feature.collect.presentation.navigation.CollectNavigationViewModel
import org.koin.compose.viewmodel.koinViewModel

class CollectFeatureEntryImpl : CollectFeatureEntry {

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    @Composable
    override fun Host(
        onExternalOutcome: (CollectExternalOutcome) -> Unit,
    ) {
        val collectNavigationViewModel: CollectNavigationViewModel = koinViewModel()
        val sceneStrategy = rememberListDetailSceneStrategy<NavKey>()

        val state by collectNavigationViewModel.viewState.collectAsStateWithLifecycle()

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
            onBack = {
                collectNavigationViewModel.setEvent(CollectNavigationContract.Event.PopBack)
            },
            entryDecorators = listOf(
                rememberSaveableStateHolderNavEntryDecorator(),
                rememberViewModelStoreNavEntryDecorator()
            ),
            entryProvider = entryProvider {
                registerEntries(
                    builder = this,
                    onOutcome = { outcome ->
                        collectNavigationViewModel.setEvent(
                            CollectNavigationContract.Event.Outcome(
                                outcome
                            )
                        )
                    }
                )
            }
        )
    }

    @OptIn(ExperimentalMaterial3AdaptiveApi::class)
    override fun registerEntries(
        builder: EntryProviderScope<NavKey>,
        onOutcome: (CollectOutcome) -> Unit,
    ) {
        builder.apply {
            entry<CollectFeatureDestination.Orders>(
                metadata = ListDetailSceneStrategy.listPane(),
            ) {
                OrderListScreenDestination { outcome ->
                    when (outcome) {
                        is OrderListScreenContract.Effect.Outcome.OrderSelected -> onOutcome(
                            CollectOutcome.OrderSelected(outcome.orderId)
                        )

                        is OrderListScreenContract.Effect.Outcome.OrdersSelected -> {
                            outcome.orderIds.firstOrNull()?.let { id ->
                                onOutcome(CollectOutcome.OrderSelected(id))
                            }
                        }

                        is OrderListScreenContract.Effect.Outcome.Back -> onOutcome(CollectOutcome.Back)
                        is OrderListScreenContract.Effect.Outcome.Logout -> onOutcome(CollectOutcome.Logout)
                    }
                }
            }

            entry<CollectFeatureDestination.OrderFulfilment>(
                metadata = ListDetailSceneStrategy.detailPane(),
            ) {
                OrderFulfilmentScreenDestination { effect ->
                    when (effect) {
                        is OrderFulfilmentScreenContract.Effect.Outcome.Back -> onOutcome(
                            CollectOutcome.Back
                        )

                        is OrderFulfilmentScreenContract.Effect.Outcome.Confirmed -> onOutcome(
                            CollectOutcome.Back
                        )

                        is OrderFulfilmentScreenContract.Effect.Outcome.SignatureRequested -> onOutcome(
                            CollectOutcome.SignatureRequested
                        )

                        is OrderFulfilmentScreenContract.Effect.Outcome.NavigateToOrderDetails -> onOutcome(
                            CollectOutcome.NavigateToOrderDetails(effect.invoiceNumber)
                        )
                    }
                }
            }

            entry<CollectFeatureDestination.OrderDetails>(
                metadata = ListDetailSceneStrategy.detailPane(),
            ) { destination ->

                OrderDetailsScreenDestination(invoiceNumber = destination.invoiceNumber) { outcome ->
                    when (outcome) {
                        is OrderDetailsScreenContract.Effect.Outcome.Back -> onOutcome(
                            CollectOutcome.Back
                        )
                    }
                }
            }

            entry<CollectFeatureDestination.Signature>(
                metadata = ListDetailSceneStrategy.extraPane(),
            ) {
                SignatureScreenDestination { outcome ->
                    when (outcome) {
                        is SignatureScreenContract.Effect.Outcome.Back -> onOutcome(CollectOutcome.Back)
                        is SignatureScreenContract.Effect.Outcome.SignatureSaved -> {
                            onOutcome(CollectOutcome.Back)
                        }
                    }
                }
            }
        }
    }
}