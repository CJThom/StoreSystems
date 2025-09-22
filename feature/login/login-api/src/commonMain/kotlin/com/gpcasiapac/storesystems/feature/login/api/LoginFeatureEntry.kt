package com.gpcasiapac.storesystems.feature.login.api

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.gpcasiapac.storesystems.common.presentation.navigation.FeatureEntriesRegistrar

interface LoginFeatureEntry {

    @Composable
    fun Login(modifier: Modifier, onLoggedIn: () -> Unit)

    fun registerEntries(
        registrar: FeatureEntriesRegistrar,
        onLoggedIn: () -> Unit
    )

}
