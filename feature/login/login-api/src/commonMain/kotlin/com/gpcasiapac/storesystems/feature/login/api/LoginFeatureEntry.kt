package com.gpcasiapac.storesystems.feature.login.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gpcasiapac.storesystems.common.presentation.navigation.FeatureEntriesRegistrar

interface LoginFeatureEntry {

    @Composable
    fun Login(modifier: Modifier, onLoggedIn: () -> Unit)

    // Drop-in host for this feature. The implementation can use its own MVI VM and NavDisplay.
    @Composable
    fun Host()

    fun registerEntries(
        registrar: FeatureEntriesRegistrar,
        onLoggedIn: () -> Unit
    )

}
