package com.gpcasiapac.storesystems.foundation.config.di

import com.gpcasiapac.storesystems.foundation.config.featureflags.FeatureFlagInitializer
import org.koin.dsl.module

val configModule = module {
    single(createdAtStart = true) {
        FeatureFlagInitializer(
            featureFlags = get(),
        )
    }
}