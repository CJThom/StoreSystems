package com.gpcasiapac.storesystems.external.feature_flags.data.internal

import com.gpcasiapac.storesystems.external.feature_flags.api.FeatureFlags
import com.gpcasiapac.storesystems.external.feature_flags.api.FeatureFlagConfig
import com.gpcasiapac.storesystems.external.feature_flags.api.FlagKey
import com.gpcasiapac.storesystems.external.feature_flags.api.MultiContextBuilder
import kotlinx.coroutines.flow.Flow

/**
 * Internal LaunchDarkly implementation of FeatureFlags.
 * Platform-specific implementations handle the actual LaunchDarkly SDK integration.
 */
internal expect class LaunchDarklyFeatureFlags(
    config: FeatureFlagConfig.LaunchDarkly,
    contextBuilder: MultiContextBuilder.() -> Unit
) : FeatureFlags