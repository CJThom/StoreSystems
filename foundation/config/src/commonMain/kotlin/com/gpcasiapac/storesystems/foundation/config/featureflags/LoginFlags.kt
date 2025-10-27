package com.gpcasiapac.storesystems.foundation.config.featureflags

import com.gpcasiapac.storesystems.external.feature_flags.api.FlagKey

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
        override val name: String = "collectMfaRequired"
        override val default: Boolean = true
    }

    object Mfa_V2 : FlagKey<Boolean> {
        override val name: String = "feature.login.mfa_v2"
        override val default: Boolean = true
    }

}