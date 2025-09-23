package com.gpcasiapac.storesystems.feature.collect.presentation.entry

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import com.gpcasiapac.storesystems.common.presentation.navigation.FeatureEntriesRegistrar
import com.gpcasiapac.storesystems.feature.collect.api.CollectOrdersFeatureEntry
import com.gpcasiapac.storesystems.feature.collect.presentation.orders.OrdersDestination
import com.gpcasiapac.storesystems.feature.collect.presentation.orders.OrdersViewModel

/**
 * Common implementation of CollectOrdersFeatureEntry.
 * Provides Host(). No-op registrar on non-Android targets.
 */
class CollectOrdersFeatureEntryImpl : CollectOrdersFeatureEntry {

    @Composable
    override fun Host() {
        val vm = remember { OrdersViewModel() }
        OrdersDestination(viewModel = vm) { /* no-op in common host */ }
    }

    override fun registerEntries(registrar: FeatureEntriesRegistrar) {
        // no-op on non-Android targets by default
    }
}
