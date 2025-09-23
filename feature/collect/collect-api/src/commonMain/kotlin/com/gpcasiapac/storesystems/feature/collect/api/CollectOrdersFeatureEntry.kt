package com.gpcasiapac.storesystems.feature.collect.api

import androidx.compose.runtime.Composable
import com.gpcasiapac.storesystems.common.presentation.navigation.FeatureEntriesRegistrar

interface CollectOrdersFeatureEntry {

    fun registerEntries(registrar: FeatureEntriesRegistrar)

    // Drop-in host for this feature. The implementation can use its own MVI VM and NavDisplay.
    @Composable
    fun Host()
}
