package com.gpcasiapac.storesystems.common.presentation.session

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

/**
 * Session handler that manages session state through delegation.
 * ViewModels can use this class to handle session management.
 *
 * @param initialSession The initial session state
 * @param sessionFlow Flow of session updates
 */
class SessionHandler<Session : SessionIds>(
    initialSession: Session,
    private val sessionFlow: Flow<Session>,
) : ViewModel(), SessionHandlerDelegate<Session> {
    // TODO: When Koin allows for viewModelScope injection remove ViewModel() inheritance

    private val _sessionState: MutableStateFlow<Session> = MutableStateFlow(initialSession)
    override val sessionState: StateFlow<Session> = _sessionState.asStateFlow()

    init {
        viewModelScope.launch {
            sessionFlow.collectLatest { session ->
                Logger.withTag("SessionHandler").d { session.toString() }
                _sessionState.update { session }
            }
        }
    }
}
