package com.gpcasiapac.storesystems.external.feature_flags.data

/**
 * Configuration class for LaunchDarkly feature flags service.
 * Contains the API key and other LaunchDarkly-specific settings.
 */
data class LDFeatureConfig(
    val apiKey: String,
    val initializationTimeoutMs: Int = 3_000,
    val environment: String = "production"
)