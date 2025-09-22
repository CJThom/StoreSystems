package com.gpcasiapac.storesystems.common.presentation.session

import kotlinx.coroutines.flow.StateFlow

/**
 * Delegate interface for handling session state in ViewModels.
 * ViewModels can implement this interface by delegation to manage session state.
 */
interface SessionHandlerDelegate<Session : SessionIds> {
    val sessionState: StateFlow<Session>
}
