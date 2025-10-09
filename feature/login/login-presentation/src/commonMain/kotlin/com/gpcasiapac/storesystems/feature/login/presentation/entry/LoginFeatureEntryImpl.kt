package com.gpcasiapac.storesystems.feature.login.presentation.entry

import androidx.compose.runtime.Composable
import androidx.navigation3.runtime.EntryProviderScope
import androidx.navigation3.runtime.NavKey
import com.gpcasiapac.storesystems.feature.login.api.LoginExternalOutcome
import com.gpcasiapac.storesystems.feature.login.api.LoginFeatureEntry
import com.gpcasiapac.storesystems.feature.login.api.LoginOutcome

/**
 * Common implementation of LoginFeatureEntry for non-Android targets.
 * Compose-free; provides no-op registration by default.
 */
class LoginFeatureEntryImpl : LoginFeatureEntry {

    @Composable
    override fun Host(onExternalOutcome: (LoginExternalOutcome) -> Unit) {
        /* no-op on non-Android targets for now */
    }


    override fun registerEntries(
        builder: EntryProviderScope<NavKey>,
        onOutcome: (LoginOutcome) -> Unit,
    ) { /* no-op on non-Android targets for now */ }
}
