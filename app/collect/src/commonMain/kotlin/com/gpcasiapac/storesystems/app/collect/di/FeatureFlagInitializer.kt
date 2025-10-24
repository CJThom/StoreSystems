package com.gpcasiapac.storesystems.app.collect.di

import com.gpcasiapac.storesystems.external.feature_flags.api.FeatureFlags
import kotlinx.coroutines.runBlocking

/**
 * App-level feature flag initialization.
 * 
 * This is registered in appModule (Modules.kt) with createdAtStart = true,
 * which gives it access to all repositories registered in the app.
 * 
 * Automatically initializes feature flags when created by Koin.
 */
class FeatureFlagInitializer(
    private val featureFlags: FeatureFlags,
    private val contextProvider: FeatureFlagContextProvider // Use provider for default context
) {
    init {
        // Runs automatically when Koin creates this singleton
        runBlocking {
            initialize()
        }
    }

    private suspend fun initialize() {
        featureFlags.initialize {
            // Use provider to configure complete anonymous context
            // This ensures device and organization context are always included
            contextProvider.configureCompleteAnonymousContext(this)
        }
    }
}
