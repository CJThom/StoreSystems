package com.gpcasiapac.storesystems.feature.login.domain.usecase

import com.gpcasiapac.storesystems.external.feature_flags.api.FeatureFlags
import com.gpcasiapac.storesystems.foundation.config.featureflags.LoginFlags

/**
 * Use Case: Determine if MFA is required
 * 
 * Business logic for MFA requirement based on feature flags
 */
class CheckMfaRequirementUseCase(
    private val featureFlags: FeatureFlags
) {
    operator fun invoke(): Boolean {
        val isMfaEnabled = featureFlags.isEnabled(LoginFlags.MfaRequired)
        return isMfaEnabled
    }
}
