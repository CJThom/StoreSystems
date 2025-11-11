package com.gpcasiapac.storesystems.external.feature_flags.data.internal

import com.gpcasiapac.storesystems.external.feature_flags.api.FeatureFlagConfig
import com.gpcasiapac.storesystems.foundation.config.BuildConfig
import org.koin.core.module.Module
import org.koin.dsl.module

/**
 * Platform-specific Koin Module handle the actual feature flag SDK integration.
 */
expect val featureFlagImplModule: Module

val featureFlagModule = module {
    single<FeatureFlagConfig> {
        FeatureFlagConfig.LaunchDarkly(BuildConfig.LAUNCHDARKLY_KEY, 3_000, BuildConfig.ENVIRONMENT)
    }
} + featureFlagImplModule