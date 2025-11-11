package com.gpcasiapac.storesystems.feature.login.domain.usecase

import com.gpcasiapac.storesystems.external.feature_flags.api.FeatureFlags
import com.gpcasiapac.storesystems.external.feature_flags.api.MultiContextBuilder

/**
 * Use Case: Update feature flag context after authentication
 * 
 * This UPDATES the context that was initially set by FeatureFlagInitializer
 */
class UpdateFeatureFlagContextUseCase(
    private val featureFlags: FeatureFlags
) {
    suspend operator fun invoke(username: String) {
        featureFlags.updateContext {
            configureAuthenticatedContext(username, this)
        }
    }
    
    private fun configureAuthenticatedContext(
        username: String,
        builder: MultiContextBuilder
    ) {
        // Update user context from anonymous to authenticated
        builder.user("user_session", buildMap {
            put("anonymous", false)
            put("username", username)
        })
        
        // Device context remains from initialization
        builder.device("device_information", buildMap {
            put("model", "TC53e")
        })
        
        // Organization context remains from initialization
        builder.custom("organization", "org_context", buildMap {
            put("organizationId", "GPC_ASIA_PAC")
            put("appName", "CollectApp")
            put("environment", "production")
        })
    }
}
