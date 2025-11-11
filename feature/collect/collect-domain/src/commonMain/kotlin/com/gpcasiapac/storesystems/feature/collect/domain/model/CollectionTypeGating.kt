package com.gpcasiapac.storesystems.feature.collect.domain.model

/**
 * Represents the enable/disable state for collection type options derived from the
 * current set of orders in the Work Order.
 */
data class CollectionTypeGating(
    val isStandardEnabled: Boolean,
    val isAccountEnabled: Boolean,
    val isCourierEnabled: Boolean,
)