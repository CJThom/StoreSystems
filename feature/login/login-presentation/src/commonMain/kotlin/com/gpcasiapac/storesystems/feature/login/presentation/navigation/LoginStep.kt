package com.gpcasiapac.storesystems.feature.login.presentation.navigation

import androidx.navigation3.runtime.NavKey

/**
 * Pure Kotlin navigation keys for the Login feature.
 */
sealed interface LoginStep : NavKey {
    data object Login : LoginStep
    data class Mfa(val userId: String) : LoginStep
    data class Mfa_V2(val userId: String) : LoginStep
    data object Complete : LoginStep
}
