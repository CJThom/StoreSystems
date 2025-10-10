package com.gpcasiapac.storesystems.app.collect.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

// App-level destinations (Host-only navigation)
@Serializable
sealed interface CollectAppDestination : NavKey {
    @Serializable
    data object LoginHost : CollectAppDestination

    @Serializable
    data object CollectHost : CollectAppDestination
}