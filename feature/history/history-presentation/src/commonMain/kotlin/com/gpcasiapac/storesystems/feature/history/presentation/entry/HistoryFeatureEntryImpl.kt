package com.gpcasiapac.storesystems.feature.history.presentation.entry

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey
import com.gpcasiapac.storesystems.feature.history.api.HistoryExternalOutcome
import com.gpcasiapac.storesystems.feature.history.api.HistoryFeatureEntry
import com.gpcasiapac.storesystems.feature.history.api.HistoryOutcome

/**
 * Common implementation of HistoryFeatureEntry.
 * No-op on non-Android targets by default.
 */
class HistoryFeatureEntryImpl : HistoryFeatureEntry {

    @Composable
    override fun Host(onExternalOutcome: (HistoryExternalOutcome) -> Unit) {
        // no-op on non-Android targets
    }

    override fun registerEntries(
        builder: EntryProviderBuilder<NavKey>,
        onOutcome: (HistoryOutcome) -> Unit,
    ) {
        // no-op on non-Android targets
    }
}
