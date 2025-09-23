package com.gpcasiapac.storesystems.common.presentation.navigation

import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent

/**
 * Generic navigation events for a feature back stack.
 */
sealed interface NavEvent<K : FeatureKey> : ViewEvent {
    data class Push<K : FeatureKey>(val key: K) : NavEvent<K>
    data class Pop<K : FeatureKey>(val count: Int = 1) : NavEvent<K>
    data class Replace<K : FeatureKey>(val key: K) : NavEvent<K>
}