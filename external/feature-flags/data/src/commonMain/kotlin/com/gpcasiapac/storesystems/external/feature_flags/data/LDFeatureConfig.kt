package com.gpcasiapac.storesystems.external.feature_flags.data

import com.gpcasiapac.storesystems.external.feature_flags.api.FeatureFlagConfig

data class LDFeatureConfig(
    val apiKey: String,
    val initializationTimeoutMs: Int = 3_000,
    val environment: String = "production"
    //TODO Add app meta data?
) : FeatureFlagConfig