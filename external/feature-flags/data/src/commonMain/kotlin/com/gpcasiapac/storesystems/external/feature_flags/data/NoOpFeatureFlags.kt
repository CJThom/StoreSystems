package com.gpcasiapac.storesystems.external.feature_flags.data

import com.gpcasiapac.storesystems.external.feature_flags.api.FeatureFlags
import com.gpcasiapac.storesystems.external.feature_flags.api.FlagKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

/**
 * No-operation implementation of FeatureFlags that returns default values.
 * Useful for testing, development, or when feature flags are not needed.
 */
internal class NoOpFeatureFlags : FeatureFlags {
    override fun isEnabled(key: FlagKey<Boolean>): Boolean = key.default
    override fun getString(key: FlagKey<String>): String = key.default
    override fun getInt(key: FlagKey<Int>): Int = key.default
    override fun getDouble(key: FlagKey<Double>): Double = key.default
    
    override fun observeBoolean(key: FlagKey<Boolean>): Flow<Boolean> = flowOf(key.default)
    override fun observeString(key: FlagKey<String>): Flow<String> = flowOf(key.default)
    override fun observeInt(key: FlagKey<Int>): Flow<Int> = flowOf(key.default)
    override fun observeDouble(key: FlagKey<Double>): Flow<Double> = flowOf(key.default)
}