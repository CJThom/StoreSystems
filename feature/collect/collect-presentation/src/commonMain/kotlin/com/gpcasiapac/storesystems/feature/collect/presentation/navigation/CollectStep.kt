package com.gpcasiapac.storesystems.feature.collect.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.gpcasiapac.storesystems.common.presentation.navigation.FeatureKey

/**
 * Pure Kotlin navigation keys for the Collect feature.
 */
sealed interface CollectStep : NavKey {
    data object Orders : CollectStep
    data class OrderDetails(val orderId: String) : CollectStep
    // You can add more steps as the flow grows (e.g., ScanItem, ConfirmPickup, etc.)
}
