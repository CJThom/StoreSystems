package com.gpcasiapac.storesystems.app.superapp.navigation

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface PickingFeatureDestination : NavKey {
    @Serializable
    data object Root : PickingFeatureDestination
}
