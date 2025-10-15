package com.gpcasiapac.storesystems.external.feature_flags.api

import kotlinx.coroutines.flow.Flow

interface FeatureFlagService<Config : FeatureFlagConfig> {
    fun initialize(config: Config, contextBuilder: MultiContextBuilder.() -> Unit = {}): Boolean

    fun getFeatureBoolean(key: FlagKey<Boolean>): Boolean
    fun getFeatureString(key: FlagKey<String>): String
    fun getFeatureInt(key: FlagKey<Int>): Int
    fun getFeatureDouble(key: FlagKey<Double>): Double

    fun observeFeatureString(key: FlagKey<String>): Flow<String>
    fun observeFeatureInt(key: FlagKey<Int>): Flow<Int>
    fun observeFeatureDouble(key: FlagKey<Double>): Flow<Double>
    fun observeFeatureBoolean(key: FlagKey<Boolean>): Flow<Boolean>


}