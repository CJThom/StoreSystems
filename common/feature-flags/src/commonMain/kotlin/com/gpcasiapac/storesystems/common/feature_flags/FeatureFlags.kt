package com.gpcasiapac.storesystems.common.feature_flags

import kotlinx.coroutines.flow.Flow

/**
 * Read-only feature flags interface.
 */
interface FeatureFlags {
    fun <T> get(key: FlagKey<T>): T
    fun <T> observe(key: FlagKey<T>): Flow<T>

    fun isEnabled(key: FlagKey<Boolean>): Boolean = get(key)
}
