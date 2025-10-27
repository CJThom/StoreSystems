package com.gpcasiapac.storesystems.foundation.config.featureflags

/**
 * Constants for feature flag context configuration.
 */
object FeatureFlagConstants {
    // Context Keys
    const val DEVICE_CONTEXT_KEY = "device_information"
    const val ORGANIZATION_CONTEXT_TYPE = "organization"
    const val ORGANIZATION_CONTEXT_KEY = "org_context"
    const val USER_CONTEXT_KEY = "user_session"

    // Organization Context
    const val ORGANIZATION_ID_KEY = "organizationId"
    const val DEFAULT_ORGANIZATION_ID = "GPC_ASIA_PAC"
    const val APP_NAME_KEY = "appName"
    const val DEFAULT_APP_NAME = "CollectApp"
    const val ENVIRONMENT_KEY = "environment"
    const val DEFAULT_ENVIRONMENT = "production"
    
    // User Context
    const val ANONYMOUS_KEY = "anonymous"
}
