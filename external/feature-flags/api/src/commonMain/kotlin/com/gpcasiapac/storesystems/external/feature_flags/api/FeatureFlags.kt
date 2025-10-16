package com.gpcasiapac.storesystems.external.feature_flags.api

import kotlinx.coroutines.flow.Flow

/**
 * Clean, non-generic interface for feature flags.
 * Provides type-safe access to feature flag values without exposing implementation details.
 */
interface FeatureFlags {
    fun initialize(
        contextBuilder: MultiContextBuilder.() -> Unit
    ): Boolean

    fun updateContext(contextBuilder: MultiContextBuilder.() -> Unit)
    fun isEnabled(key: FlagKey<Boolean>): Boolean
    fun getString(key: FlagKey<String>): String
    fun getInt(key: FlagKey<Int>): Int
    fun getDouble(key: FlagKey<Double>): Double

    fun observeBoolean(key: FlagKey<Boolean>): Flow<Boolean>
    fun observeString(key: FlagKey<String>): Flow<String>
    fun observeInt(key: FlagKey<Int>): Flow<Int>
    fun observeDouble(key: FlagKey<Double>): Flow<Double>
}