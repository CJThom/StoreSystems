package com.gpcasiapac.storesystems.feature.login.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface LoginFeatureDestination : NavKey {
    @Serializable
    data object Login : LoginFeatureDestination

    @Serializable
    data class Otp(val userId: String) : LoginFeatureDestination
}
