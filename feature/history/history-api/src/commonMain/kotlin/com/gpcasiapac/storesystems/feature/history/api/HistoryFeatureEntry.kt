package com.gpcasiapac.storesystems.feature.history.api

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey

interface HistoryFeatureEntry {

    fun registerEntries(
        builder: EntryProviderBuilder<NavKey>,
        onOutcome: (HistoryOutcome) -> Unit,
    )

    @Composable
    fun Host(
        onExternalOutcome: (HistoryExternalOutcome) -> Unit,
    )
}
