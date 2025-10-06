package com.gpcasiapac.storesystems.feature.collect.api

/**
 * External outcomes that the app shell should observe from the Collect feature.
 * Keep this minimal; add items only when the app needs to react.
 */
sealed interface CollectExternalOutcome {
    /** Example placeholder: ask the app/tabs host to open a scanner UI */
    data object OpenScanner : CollectExternalOutcome
    
    /** Request the app to navigate back to the login screen (logout) */
    data object Logout : CollectExternalOutcome
}