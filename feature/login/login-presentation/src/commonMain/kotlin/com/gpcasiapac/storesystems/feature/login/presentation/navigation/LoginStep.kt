package com.gpcasiapac.storesystems.feature.login.presentation.navigation

import com.gpcasiapac.storesystems.common.presentation.navigation.FeatureKey

/**
 * Pure Kotlin navigation keys for the Login feature.
 */
sealed interface LoginStep : FeatureKey {
    data object Login : LoginStep
    data class Mfa(val userId: String) : LoginStep
    data class Mfa_V2(val userId: String) : LoginStep
    data object Complete : LoginStep
}
