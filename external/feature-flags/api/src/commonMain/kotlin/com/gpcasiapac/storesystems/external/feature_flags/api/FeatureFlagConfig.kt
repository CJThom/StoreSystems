package com.gpcasiapac.storesystems.external.feature_flags.api

/**
 * Sealed configuration classes for type-safe feature flag provider setup.
 * Each implementation type has its own specific configuration parameters.
 */
sealed interface FeatureFlagConfig {
    data class LaunchDarkly(
        val apiKey: String,
        val initializationTimeoutMs: Int = 3_000,
        val environment: String = "production"
    ) : FeatureFlagConfig
    
    data class Firebase(
        val projectId: String,
        val apiKey: String,
        val cachingEnabled: Boolean = true
    ) : FeatureFlagConfig
    
    data object NoOp : FeatureFlagConfig
}