package com.gpcasiapac.storesystems.app.collect.navigation

import androidx.navigation3.runtime.NavKey
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.feature.collect.api.CollectOutcome
import com.gpcasiapac.storesystems.feature.login.api.LoginOutcome

/**
 * Global app-level navigation contract for the Collect app when using a single back stack
 * that mixes keys from Login and Collect features.
 */
object CollectGlobalNavContract {
    sealed interface Event : ViewEvent {
        // Wrap feature outcomes so the app VM can exhaustively handle them
        data class FromLogin(val outcome: LoginOutcome) : Event
        data class FromCollect(val outcome: CollectOutcome) : Event

        // Generic stack commands (kept for completeness, may be unused now)
        data class Push(val key: NavKey) : Event
        data class ReplaceTop(val key: NavKey) : Event

        // App-global controls
        data class PopBack(val count: Int = 1) : Event
    }
}
