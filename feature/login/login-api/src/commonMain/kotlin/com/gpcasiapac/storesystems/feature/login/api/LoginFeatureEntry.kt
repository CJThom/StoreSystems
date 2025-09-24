package com.gpcasiapac.storesystems.feature.login.api

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderBuilder
import androidx.navigation3.runtime.NavKey

interface LoginFeatureEntry {

    @Composable
    fun Host(onComplete: () -> Unit)

    fun registerEntries(
        builder: EntryProviderBuilder<NavKey>,
        onOutcome: (LoginOutcome) -> Unit,
    )
}
