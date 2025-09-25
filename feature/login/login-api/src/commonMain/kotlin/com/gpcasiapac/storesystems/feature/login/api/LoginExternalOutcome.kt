package com.gpcasiapac.storesystems.feature.login.api

/**
 * External outcomes that the app shell should observe from the Login feature.
 * Keep this minimal; add items only when the app needs to react.
 */
sealed interface LoginExternalOutcome {
    data object LoginCompleted : LoginExternalOutcome
}
