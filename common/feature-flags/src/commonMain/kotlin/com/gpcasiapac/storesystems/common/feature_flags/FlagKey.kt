package com.gpcasiapac.storesystems.common.feature_flags

/**
 * Simple type-safe feature flag key contract for KMP.
 */
interface FlagKey<T> {
    val name: String
    val default: T
}
