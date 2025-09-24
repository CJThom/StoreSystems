package com.gpcasiapac.storesystems.feature.login.presentation.navigation

import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent

/**
 * Legacy, feature-scoped navigation contract for the Login host.
 * Aligned to outcome-style naming to match global navigation patterns.
 */
object LoginNavContract {
    sealed interface Event : ViewEvent {
        // Outcome-style naming (no destination keywords)
        data class MfaRequired(val userId: String) : Event
        data object LoginCompleted : Event
        data class PopBack(val count: Int = 1) : Event
    }
}