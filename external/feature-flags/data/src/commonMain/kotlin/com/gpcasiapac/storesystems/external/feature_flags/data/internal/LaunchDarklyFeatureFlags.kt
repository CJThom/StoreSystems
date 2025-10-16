package com.gpcasiapac.storesystems.external.feature_flags.data.internal

import com.gpcasiapac.storesystems.external.feature_flags.api.FeatureFlagConfig
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Internal LaunchDarkly implementation of FeatureFlags.
 * Platform-specific implementations handle the actual LaunchDarkly SDK integration.
 */
expect val featureFlagImplModule: Module

val featureFlagModule = module {
    single<FeatureFlagConfig> {
        FeatureFlagConfig.LaunchDarkly("", 3_000, "production")
    }
} + featureFlagImplModule