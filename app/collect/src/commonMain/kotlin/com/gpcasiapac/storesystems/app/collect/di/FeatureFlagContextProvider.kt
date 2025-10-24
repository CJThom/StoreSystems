package com.gpcasiapac.storesystems.app.collect.di

import com.gpcasiapac.storesystems.external.feature_flags.api.MultiContextBuilder

/**
 * Provides default/static feature flag context that doesn't change across operations.
 * 
 * This ensures:
 * - Consistent context configuration across the app
 * - Single source of truth for default context
 * - DRY principle (no repetition of device/org context)
 * 
 * Dependencies can be injected for dynamic values (e.g., DeviceInfoRepository)
 */
class FeatureFlagContextProvider(
    // Future: Inject repositories for dynamic context
    // private val deviceInfoRepository: DeviceInfoRepository? = null,
) {
    /**
     * Configure default device context that doesn't change
     */
    fun configureDeviceContext(builder: MultiContextBuilder) {
        builder.device("device_information", buildMap {
            put("model", "TC53e")
            // Future: Add dynamic device info from repository
            // deviceInfoRepository?.getDeviceInfo()?.let {
            //     put("os_version", it.osVersion)
            //     put("app_version", it.appVersion)
            //     put("battery_level", it.batteryLevel)
            // }
        })
    }

    /**
     * Configure default organization context (app-level, static)
     */
    fun configureOrganizationContext(builder: MultiContextBuilder) {
        builder.custom("organization", "org_context", buildMap {
            put("organizationId", "GPC_ASIA_PAC")
            put("appName", "CollectApp")
            put("environment", "production")
        })
    }

    /**
     * Configure anonymous user context (for initialization or logout)
     */
    fun configureAnonymousUserContext(builder: MultiContextBuilder) {
        builder.user("user_session", buildMap {
            put("anonymous", true)
        })
    }

    /**
     * Configure authenticated user context
     */
    fun configureAuthenticatedUserContext(
        username: String,
        builder: MultiContextBuilder
    ) {
        builder.user("user_session", buildMap {
            put("anonymous", false)
            put("username", username)
        })
    }

    /**
     * Apply all default contexts (device + organization)
     * This should be called in every context update to ensure consistency
     */
    fun applyDefaultContexts(builder: MultiContextBuilder) {
        configureDeviceContext(builder)
        configureOrganizationContext(builder)
    }

    /**
     * Configure complete anonymous context (default + anonymous user)
     */
    fun configureCompleteAnonymousContext(builder: MultiContextBuilder) {
        configureAnonymousUserContext(builder)
        applyDefaultContexts(builder)
    }

    /**
     * Configure complete authenticated context (default + authenticated user)
     */
    fun configureCompleteAuthenticatedContext(
        username: String,
        builder: MultiContextBuilder
    ) {
        configureAuthenticatedUserContext(username, builder)
        applyDefaultContexts(builder)
    }
}
