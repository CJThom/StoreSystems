package com.gpcasiapac.storesystems.app.collect.navigation

import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.feature.collect.presentation.navigation.CollectNavContract
import com.gpcasiapac.storesystems.feature.login.presentation.navigation.LoginNavContract

/**
 * Global app-level navigation contract for the Collect app when using a single back stack
 * that mixes keys from Login and Collect features.
 */
object CollectGlobalNavContract {
    sealed interface Event : ViewEvent {
        // Wrap feature outcomes so the app VM can exhaustively handle them
        data class FromLogin(val outcome: com.gpcasiapac.storesystems.feature.login.api.LoginOutcome) : Event
        data class FromCollect(val outcome: com.gpcasiapac.storesystems.feature.collect.api.CollectOutcome) : Event

        // Generic stack commands (kept for completeness, may be unused now)
        data class Push(val key: androidx.navigation3.runtime.NavKey) : Event
        data class ReplaceTop(val key: androidx.navigation3.runtime.NavKey) : Event

        // App-global controls
        data class PopBack(val count: Int = 1) : Event
    }
}
