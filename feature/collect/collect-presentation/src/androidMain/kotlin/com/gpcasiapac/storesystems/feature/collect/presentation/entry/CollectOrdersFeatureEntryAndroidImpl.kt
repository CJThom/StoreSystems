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
        // Delegate to the feature's VM-driven host
        CollectHost()
    }

    override fun registerEntries(
        builder: androidx.navigation3.runtime.EntryProviderBuilder<androidx.navigation3.runtime.NavKey>,
        onOutcome: (com.gpcasiapac.storesystems.feature.collect.api.CollectOutcome) -> Unit,
    ) {
        builder.apply {
            entry<CollectFeatureDestination.Orders> {
                OrdersDestination { navigationEffect ->
                    when (navigationEffect) {
                        is OrdersScreenContract.Effect.Navigation.OrderSelected -> onOutcome(
                            com.gpcasiapac.storesystems.feature.collect.api.CollectOutcome.OrderSelected(navigationEffect.orderId)
                        )
                    }
                }
            }

            entry<CollectFeatureDestination.OrderDetails> { d ->
                OrderDetailsScreen(
                    orderId = d.orderId,
                    onBack = { onOutcome(com.gpcasiapac.storesystems.feature.collect.api.CollectOutcome.Back) },
                )
            }
        }
    }
}
