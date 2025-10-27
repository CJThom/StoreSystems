package com.gpcasiapac.storesystems.foundation.config.featureflags

import com.gpcasiapac.storesystems.external.feature_flags.api.FeatureFlags
import com.gpcasiapac.storesystems.external.feature_flags.api.MultiContextBuilder
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
) {
    init {
        // Runs automatically when Koin creates this singleton
        runBlocking {
            initialize()
        }
    }

    private suspend fun initialize() {
        featureFlags.initialize {
            configureOrganizationContext(this)
            configureAnonymousUserContext(this)
        }
    }

    /**
     * Configure default organization context (app-level, static)
     */
    fun configureOrganizationContext(builder: MultiContextBuilder) {
        builder.custom(FeatureFlagConstants.ORGANIZATION_CONTEXT_TYPE, FeatureFlagConstants.ORGANIZATION_CONTEXT_KEY, buildMap {
            put(FeatureFlagConstants.ORGANIZATION_ID_KEY, FeatureFlagConstants.DEFAULT_ORGANIZATION_ID)
            put(FeatureFlagConstants.APP_NAME_KEY, FeatureFlagConstants.DEFAULT_APP_NAME)
            put(FeatureFlagConstants.ENVIRONMENT_KEY, FeatureFlagConstants.DEFAULT_ENVIRONMENT)
        })
    }

    /**
     * Configure anonymous user context (for initialization or logout)
     */
    fun configureAnonymousUserContext(builder: MultiContextBuilder) {
        builder.user(FeatureFlagConstants.USER_CONTEXT_KEY, buildMap {
            put(FeatureFlagConstants.ANONYMOUS_KEY, true)
        })
    }


}
