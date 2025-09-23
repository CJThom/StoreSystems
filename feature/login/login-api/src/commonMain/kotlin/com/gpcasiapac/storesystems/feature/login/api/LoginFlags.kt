package com.gpcasiapac.storesystems.feature.login.api

import com.gpcasiapac.storesystems.common.feature_flags.FlagKey

/**
 * Type-safe keys for toggling Login feature behavior.
 */
object LoginFlags {
    /** Controls whether the Login feature is available/visible. */
    object Enabled : FlagKey<Boolean> {
        override val name: String = "feature.login.enabled"
        override val default: Boolean = true
    }

    /** Demo: require MFA step after successful login (no real MFA flow yet). */
    object MfaRequired : FlagKey<Boolean> {
        override val name: String = "feature.login.mfa_required"
        override val default: Boolean = true
    }

    object Mfa_V2 : FlagKey<Boolean> {
        override val name: String = "feature.login.mfa_v2"
        override val default: Boolean = true
    }

}