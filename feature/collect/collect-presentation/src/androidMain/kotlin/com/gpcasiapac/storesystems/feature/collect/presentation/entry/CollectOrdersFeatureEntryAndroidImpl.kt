package com.gpcasiapac.storesystems.feature.collect.presentation.entry

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import androidx.navigation3.runtime.entry
import com.gpcasiapac.storesystems.feature.collect.api.CollectFeatureDestination
import com.gpcasiapac.storesystems.feature.collect.api.CollectOrdersFeatureEntry
import com.gpcasiapac.storesystems.feature.collect.api.CollectOutcome
import com.gpcasiapac.storesystems.feature.collect.presentation.details.OrderDetailsScreen
import com.gpcasiapac.storesystems.feature.collect.presentation.orders.OrdersDestination
import com.gpcasiapac.storesystems.feature.collect.presentation.orders.OrdersScreenContract

class CollectOrdersFeatureEntryAndroidImpl : CollectOrdersFeatureEntry {

    @Composable
    override fun Host() {
        // Delegate to the feature's VM-driven host
        CollectHost()
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
