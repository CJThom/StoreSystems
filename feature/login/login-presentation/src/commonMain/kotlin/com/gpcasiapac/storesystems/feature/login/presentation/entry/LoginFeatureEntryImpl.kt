package com.gpcasiapac.storesystems.feature.login.presentation.entry

import androidx.compose.runtime.Composable
import com.gpcasiapac.storesystems.common.presentation.navigation.FeatureEntriesRegistrar
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureEntry

/**
 * Common implementation of LoginFeatureEntry for non-Android targets.
 * Compose-free; provides no-op registration by default.
 */
class LoginFeatureEntryImpl : LoginFeatureEntry {

    @Composable
    override fun Host(onComplete: () -> Unit) {
        /* no-op on non-Android targets for now */
    }


    override fun registerEntries(
        registrar: FeatureEntriesRegistrar,
        onLoggedIn: () -> Unit
    ) { /* no-op on non-Android targets for now */ }
}
