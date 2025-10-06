package com.gpcasiapac.storesystems.common.presentation.navigation

import androidx.navigation3.runtime.NavKey
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewEvent
import com.gpcasiapac.storesystems.common.presentation.mvi.ViewSideEffect

/**
 * Base ViewModel that manages a navigation back stack as immutable list in state.
 *
 * - Compose-free and UI-agnostic
 * - Single source of truth lives in the ViewModel
 * - Backed by simple reducer helpers for predictable, testable behavior
 * - Generic state support allows features to extend with additional properties
 */
abstract class BaseNavViewModel<Event : ViewEvent, K : NavKey, State : ViewStateWithNavigation> :
    MVIViewModel<Event, State, ViewSideEffect>() {

    /**
     * Provide the starting key shown as the root of the back stack.
     * Using a function avoids subclass property initialization order issues during base init.
     */
    protected abstract fun provideStartKey(): K
    
    /**
     * Create the initial state with the starting navigation key.
     * Features can override this to provide their extended initial state.
     */
    protected open fun createInitialState(): State = 
        createStateWithStack(listOf(provideStartKey()))
    
    /**
     * Abstract method that features must implement to create their state type
     * with the given navigation stack.
     */
    protected abstract fun createStateWithStack(stack: List<NavKey>): State

    override fun setInitialState(): State = createInitialState()

    override fun onStart() { /* no-op by default */ }

    // Protected helpers for subclasses to update the back stack from their handleEvents()
    protected fun push(key: K) = setState { 
        createStateWithStack(BackStackReducer.push(stack, key))
    }
    
    protected fun pop(count: Int = 1) = setState { 
        createStateWithStack(BackStackReducer.pop(stack, count))
    }
    
    protected fun replaceTop(key: K) = setState { 
        createStateWithStack(BackStackReducer.replaceTop(stack, key))
    }

}