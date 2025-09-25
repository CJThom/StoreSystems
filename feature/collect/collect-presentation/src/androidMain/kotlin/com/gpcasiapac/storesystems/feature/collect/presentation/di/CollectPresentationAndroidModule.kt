package com.gpcasiapac.storesystems.feature.collect.presentation.di

import com.gpcasiapac.storesystems.feature.collect.api.CollectOrdersFeatureEntry
import com.gpcasiapac.storesystems.feature.collect.presentation.entry.CollectOrdersFeatureEntryAndroidImpl
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.bind
import org.koin.dsl.module

/**
 * Android-specific Koin module for the Collect presentation layer.
 * Registers ViewModels so they can be obtained via koinViewModel(),
 * and binds the Android FeatureEntry implementation (overriding common if needed).
 */
val collectPresentationAndroidModule = module {
    // Bind Android-specific FeatureEntry (overrides common CollectOrdersFeatureEntryImpl)
    singleOf(::CollectOrdersFeatureEntryAndroidImpl).bind<CollectOrdersFeatureEntry>()
}
