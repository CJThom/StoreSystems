package com.gpcasiapac.storesystems.external.feature_flags.data

import com.gpcasiapac.storesystems.external.feature_flags.api.FeatureFlags
import com.gpcasiapac.storesystems.external.feature_flags.api.FeatureFlagConfig
import com.gpcasiapac.storesystems.external.feature_flags.api.MultiContextBuilder
import com.gpcasiapac.storesystems.external.feature_flags.data.internal.LaunchDarklyFeatureFlags

/**
 * Factory for creating FeatureFlags instances with type-safe configuration.
 * Hides implementation details and provides clean API for consumers.
 */
object FeatureFlagsFactory {
    
    fun create(
        config: FeatureFlagConfig,
        contextBuilder: MultiContextBuilder.() -> Unit = {}
    ): FeatureFlags {
        return when (config) {
            is FeatureFlagConfig.LaunchDarkly -> LaunchDarklyFeatureFlags(config, contextBuilder)
            is FeatureFlagConfig.Firebase -> createFirebaseImpl(config, contextBuilder)
            is FeatureFlagConfig.NoOp -> NoOpFeatureFlags()
        }
    }
    
    fun createNoOp(): FeatureFlags = NoOpFeatureFlags()
    
    private fun createFirebaseImpl(
        config: FeatureFlagConfig.Firebase,
        contextBuilder: MultiContextBuilder.() -> Unit
    ): FeatureFlags {
        // Future implementation - could be FirebaseFeatureFlags (internal)
        throw NotImplementedError("Firebase implementation not yet available")
    }
}