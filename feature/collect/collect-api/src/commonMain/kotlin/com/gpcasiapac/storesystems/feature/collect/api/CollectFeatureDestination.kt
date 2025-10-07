package com.gpcasiapac.storesystems.feature.collect.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface CollectFeatureDestination : NavKey {
    @Serializable
    data object Orders : CollectFeatureDestination

    @Serializable
    data object OrderDetails : CollectFeatureDestination

    @Serializable
    data object Signature : CollectFeatureDestination
}
