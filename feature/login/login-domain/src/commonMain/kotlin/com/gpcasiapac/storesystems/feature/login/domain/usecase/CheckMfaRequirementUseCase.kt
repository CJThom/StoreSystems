package com.gpcasiapac.storesystems.feature.login.domain.usecase

import com.gpcasiapac.storesystems.external.feature_flags.api.FeatureFlags
import com.gpcasiapac.storesystems.feature.login.api.LoginFlags

/**
 * Use Case: Determine if MFA is required
 * 
 * Business logic for MFA requirement based on feature flags
 */
class CheckMfaRequirementUseCase(
    private val featureFlags: FeatureFlags
) {
    operator fun invoke(): Result {
        val isMfaEnabled = featureFlags.isEnabled(LoginFlags.MfaRequired)
        
        return if (isMfaEnabled) {
            val version = if (featureFlags.isEnabled(LoginFlags.Mfa_V2)) "v2" else "v1"
            Result.Required(version)
        } else {
            Result.NotRequired
        }
    }
    
    sealed interface Result {
        data class Required(val version: String) : Result
        data object NotRequired : Result
    }
}
