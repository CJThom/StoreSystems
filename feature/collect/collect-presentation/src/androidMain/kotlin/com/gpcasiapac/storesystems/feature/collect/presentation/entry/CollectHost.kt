package com.gpcasiapac.storesystems.feature.collect.presentation.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.navigation3.rememberViewModelStoreNavEntryDecorator
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.runtime.rememberSavedStateNavEntryDecorator
import androidx.navigation3.ui.NavDisplay
import com.gpcasiapac.storesystems.feature.collect.presentation.details.OrderDetailsScreen
import com.gpcasiapac.storesystems.feature.collect.presentation.navigation.CollectNavContract
import com.gpcasiapac.storesystems.feature.collect.presentation.navigation.CollectNavigationViewModel
import com.gpcasiapac.storesystems.feature.collect.presentation.navigation.CollectStep
import com.gpcasiapac.storesystems.feature.collect.presentation.orders.OrdersDestination
import com.gpcasiapac.storesystems.feature.collect.presentation.orders.OrdersScreenContract
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun CollectHost(
    collectNavigationViewModel: CollectNavigationViewModel = koinViewModel(),
) {
    val state by collectNavigationViewModel.viewState.collectAsState()

    NavDisplay(
        backStack = state.stack,
        onBack = { count -> collectNavigationViewModel.setEvent(CollectNavContract.Event.PopBack(count)) },
        entryDecorators = listOf(
            rememberSavedStateNavEntryDecorator(),
            rememberViewModelStoreNavEntryDecorator()
        ),
        entryProvider = entryProvider {
            entry<CollectStep.Orders> {
                // Orders screen destination
                OrdersDestination(
                    viewModel = koinViewModel(),
                    onNavigationRequested = { nav ->
                        when (nav) {
                            is OrdersScreenContract.Effect.Navigation.NavigateToOrderDetails ->
                                collectNavigationViewModel.setEvent(
                                    CollectNavContract.Event.ToOrderDetails(nav.orderId)
                                )
                        }
                    }
                )
            }
            entry<CollectStep.OrderDetails> { details ->
                // Order details destination
                OrderDetailsScreen(
                    orderId = details.orderId,
                    onBack = { collectNavigationViewModel.setEvent(CollectNavContract.Event.PopBack()) },
                )
            }
        }
    )
}
