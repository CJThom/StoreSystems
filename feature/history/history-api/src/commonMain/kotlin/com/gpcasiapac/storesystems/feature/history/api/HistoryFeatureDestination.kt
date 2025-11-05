package com.gpcasiapac.storesystems.feature.history.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface HistoryFeatureDestination : NavKey {
    @Serializable
    data object History : HistoryFeatureDestination

    @Serializable
    data class HistoryDetails(
        val type: HistoryType,
        val id: String
    ) : HistoryFeatureDestination
}
