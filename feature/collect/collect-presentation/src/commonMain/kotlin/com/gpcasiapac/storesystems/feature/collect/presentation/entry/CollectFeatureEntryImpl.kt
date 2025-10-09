package com.gpcasiapac.storesystems.feature.collect.presentation.entry

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.gpcasiapac.storesystems.feature.collect.api.CollectExternalOutcome
import com.gpcasiapac.storesystems.feature.collect.api.CollectFeatureEntry
import com.gpcasiapac.storesystems.feature.collect.api.CollectOutcome

/**
 * Common implementation of CollectFeatureEntry.
 * Provides Host(). No-op registrar on non-Android targets.
 */
class CollectFeatureEntryImpl : CollectFeatureEntry {

    @Composable
    override fun Host(
        onExternalOutcome: (CollectExternalOutcome) -> Unit,
    ) {
        // no-op on non-Android targets by default
    }

    override fun registerEntries(
        builder: EntryProviderScope <NavKey>,
        onOutcome: (CollectOutcome) -> Unit,
    ) {
        // no-op on non-Android targets by default
    }
}
