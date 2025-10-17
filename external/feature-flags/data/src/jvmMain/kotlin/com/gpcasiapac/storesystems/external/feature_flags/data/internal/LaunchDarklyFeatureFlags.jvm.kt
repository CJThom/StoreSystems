package com.gpcasiapac.storesystems.external.feature_flags.data.internal

import com.gpcasiapac.storesystems.external.feature_flags.api.FeatureFlagConfig
import org.koin.core.module.Module
import org.koin.dsl.module


actual val featureFlagImplModule: Module = module {
    single {
        val config: FeatureFlagConfig = get()
        when (config) {
            is FeatureFlagConfig.Firebase -> FeatureFlagConfig.NoOp
            is FeatureFlagConfig.LaunchDarkly -> LaunchDarklyFeatureFlagsDesktopImpl(
                config,
            )

            FeatureFlagConfig.NoOp -> FeatureFlagConfig.NoOp
        }
    }
}
