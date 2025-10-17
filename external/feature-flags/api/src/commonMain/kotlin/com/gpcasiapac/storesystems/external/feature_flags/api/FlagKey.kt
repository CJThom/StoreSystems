package com.gpcasiapac.storesystems.external.feature_flags.api

/**
 * Simple type-safe feature flag key contract for KMP.
 */
interface FlagKey<T> {
    val name: String
    val default: T
}
