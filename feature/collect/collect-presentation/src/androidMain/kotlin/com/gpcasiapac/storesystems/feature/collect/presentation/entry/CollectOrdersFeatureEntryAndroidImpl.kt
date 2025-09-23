package com.gpcasiapac.storesystems.feature.collect.presentation.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.navigation3.runtime.entry
import androidx.navigation3.runtime.rememberNavBackStack
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entryProvider
import androidx.navigation3.ui.NavDisplay
import androidx.navigation3.ui.rememberSceneSetupNavEntryDecorator
import com.gpcasiapac.storesystems.common.presentation.navigation.FeatureEntriesRegistrar
import com.gpcasiapac.storesystems.feature.collect.api.CollectFeatureDestination
import com.gpcasiapac.storesystems.feature.collect.api.CollectOrdersFeatureEntry
import com.gpcasiapac.storesystems.feature.collect.presentation.details.OrderDetailsScreen
import com.gpcasiapac.storesystems.feature.collect.presentation.orders.OrdersDestination
import com.gpcasiapac.storesystems.feature.collect.presentation.orders.OrdersScreenContract
import com.gpcasiapac.storesystems.feature.collect.presentation.orders.OrdersViewModel

class CollectOrdersFeatureEntryAndroidImpl : CollectOrdersFeatureEntry {

    @Composable
    override fun Host() {
        // Internal Navigation3 NavDisplay for Collect feature
        val backStack = rememberNavBackStack<NavKey>(CollectFeatureDestination.Orders)

        NavDisplay(
            backStack = backStack,
            onBack = { backStack.removeLastOrNull() },
            entryDecorators = listOf(rememberSceneSetupNavEntryDecorator()),
            entryProvider = entryProvider {
                entry<CollectFeatureDestination.Orders> {
                    val vm = remember { OrdersViewModel() }
                    OrdersDestination(viewModel = vm) { nav ->
                        when (nav) {
                            is OrdersScreenContract.Effect.Navigation.NavigateToOrderDetails ->
                                backStack.add(CollectFeatureDestination.OrderDetails(nav.orderId))
                        }
                    }
                }

                entry<CollectFeatureDestination.OrderDetails> { details ->
                    OrderDetailsScreen(
                        orderId = details.orderId,
                        onBack = { backStack.removeLastOrNull() },
                    )
                }
            }
        )
    }

    override fun registerEntries(registrar: FeatureEntriesRegistrar) {
        registrar.builder.apply {
            entry<CollectFeatureDestination.Orders> {
                val vm = OrdersViewModel()
                OrdersDestination(viewModel = vm) { nav ->
                    when (nav) {
                        is OrdersScreenContract.Effect.Navigation.NavigateToOrderDetails ->
                            registrar.push(CollectFeatureDestination.OrderDetails(nav.orderId))
                    }
                }
            }

            entry<CollectFeatureDestination.OrderDetails> { d ->
                OrderDetailsScreen(
                    orderId = d.orderId,
                    onBack = { registrar.pop() },
                )
            }
        }
    }
}
