package com.gpcasiapac.storesystems.feature.collect.api

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.gpcasiapac.storesystems.feature.collect.api.CollectOutcome

interface CollectOrdersFeatureEntry {

    fun registerEntries(
        builder: EntryProviderBuilder<NavKey>,
        onOutcome: (CollectOutcome) -> Unit,
    )

    // Drop-in host for this feature. The implementation can use its own MVI VM and NavDisplay.
    @Composable
    fun Host()
}
