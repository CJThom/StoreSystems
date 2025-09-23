package com.gpcasiapac.storesystems.feature.login.api

import androidx.compose.runtime.Composable
import com.gpcasiapac.storesystems.common.presentation.navigation.FeatureEntriesRegistrar

interface LoginFeatureEntry {

    @Composable
    fun Login(onLoggedIn: () -> Unit)

    // Drop-in host for this feature. The implementation can use its own MVI VM and NavDisplay.
    // Apps should prefer using Host() over registerEntries(). The callback is generic to allow
    // the app to decide what to do after a successful flow (e.g., navigate to another Host).
    @Composable
    fun Host(onSuccess: () -> Unit)

    fun registerEntries(
        registrar: FeatureEntriesRegistrar,
        onLoggedIn: () -> Unit
    )

}
