package com.gpcasiapac.storesystems.feature.login.domain.usecase

import com.gpcasiapac.storesystems.external.feature_flags.api.FeatureFlags
import com.gpcasiapac.storesystems.external.feature_flags.api.MultiContextBuilder

/**
 * Use Case: Clear feature flag context (e.g., on logout)
 * 
 * Resets context to anonymous state
 */
class ClearFeatureFlagContextUseCase(
    private val featureFlags: FeatureFlags
) {
    operator fun invoke() {
        featureFlags.updateContext {
            configureAnonymousContext(this)
        }
    }
    
    private fun configureAnonymousContext(builder: MultiContextBuilder) {
        builder.user("user_session", buildMap {
            put("anonymous", true)
        })
        builder.device("device_information", buildMap {
            put("model", "TC53e")
        })
        builder.custom("organization", "org_context", buildMap {
            put("organizationId", "GPC_ASIA_PAC")
            put("appName", "CollectApp")
            put("environment", "production")
        })
    }
}
