package com.gpcasiapac.storesystems.common.presentation.mvi

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import co.touchlab.kermit.Logger
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.onStart
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlinx.coroutines.withTimeoutOrNull

/**
 * Base ViewModel implementing the MVI (Model-View-Intent) pattern.
 *
 * @param Event The type of events this ViewModel handles
 * @param UiState The type of UI state this ViewModel manages
 * @param Effect The type of side effects this ViewModel can emit
 */
abstract class MVIViewModel<Event : ViewEvent, UiState : ViewState, Effect : ViewSideEffect> :
    ViewModel() {

    private val initialState: UiState by lazy { setInitialState() }
    abstract fun setInitialState(): UiState

    protected open suspend fun awaitReadiness(): Boolean = true
    protected open val readinessTimeoutMillis: Long = 2000L

    private val _viewState: MutableStateFlow<UiState> = MutableStateFlow(initialState)
    val viewState: StateFlow<UiState> = _viewState.onStart {
        val ready = awaitReadinessWithTimeout()
        if (ready) {
            onStart()
        } else {
            handleReadinessFailed()
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = initialState
    )

    abstract fun onStart()

    private val _event: MutableSharedFlow<Event> = MutableSharedFlow()
    private val _effect: MutableSharedFlow<Effect> = MutableSharedFlow()
    val effect: SharedFlow<Effect> = _effect

    init {
        subscribeToEvents()
    }

    fun setEvent(event: Event) {
        viewModelScope.launch { _event.emit(event) }
    }

    protected fun setState(reducer: UiState.() -> UiState) {
        _viewState.update { currentState -> currentState.reducer() }
    }

    private fun subscribeToEvents() {
        viewModelScope.launch {
            _event.collect {
                handleEvents(it)
            }
        }
    }

    abstract fun handleEvents(event: Event)

    protected fun setEffect(builder: () -> Effect) {
        val effectValue = builder()
        viewModelScope.launch { _effect.emit(effectValue) }
    }

    protected open fun handleReadinessFailed() {
        Logger.e("MVIViewModel: Readiness check failed")
        println("MVIViewModel: Readiness check failed")
    }

    private suspend fun awaitReadinessWithTimeout(): Boolean {
        return withTimeoutOrNull(readinessTimeoutMillis) {
            awaitReadiness()
        } ?: false
    }

}
