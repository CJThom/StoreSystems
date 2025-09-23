package com.gpcasiapac.storesystems.feature.login.api

import androidx.compose.runtime.Composable
import com.gpcasiapac.storesystems.common.presentation.navigation.FeatureEntriesRegistrar

interface LoginFeatureEntry {

    @Composable
    fun Host(onComplete: () -> Unit)

    fun registerEntries(
        registrar: FeatureEntriesRegistrar,
        onLoggedIn: () -> Unit
    )
}
