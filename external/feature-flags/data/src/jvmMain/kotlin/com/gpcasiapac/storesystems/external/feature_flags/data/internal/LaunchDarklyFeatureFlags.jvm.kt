package com.gpcasiapac.storesystems.external.feature_flags.data.internal

import com.gpcasiapac.storesystems.external.feature_flags.api.FeatureFlagConfig
import com.gpcasiapac.storesystems.external.feature_flags.api.FeatureFlags
import com.gpcasiapac.storesystems.external.feature_flags.data.NoOpFeatureFlags
import org.koin.core.module.Module
import org.koin.dsl.module

actual val featureFlagImplModule: Module = module {
    // Provide the platform-specific FeatureFlags implementation for JVM/Desktop
    single<FeatureFlags> {
        val config: FeatureFlagConfig = get()
        when (config) {
            is FeatureFlagConfig.Firebase -> NoOpFeatureFlags()
            is FeatureFlagConfig.LaunchDarkly -> LaunchDarklyFeatureFlagsDesktopImpl(
                config,
            )
            FeatureFlagConfig.NoOp -> NoOpFeatureFlags()
        }
    }
}
