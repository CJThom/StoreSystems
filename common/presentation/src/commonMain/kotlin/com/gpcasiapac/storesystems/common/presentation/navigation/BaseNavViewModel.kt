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
 */
abstract class BaseNavViewModel<Event : ViewEvent, K : NavKey> :
    MVIViewModel<Event, NavState<K>, ViewSideEffect>() {

    /**
     * Provide the starting key shown as the root of the back stack.
     * Using a function avoids subclass property initialization order issues during base init.
     */
    protected abstract fun provideStartKey(): K

    override fun setInitialState(): NavState<K> = NavState(stack = listOf(provideStartKey()))

    override fun onStart() { /* no-op by default */ }

    // Protected helpers for subclasses to update the back stack from their handleEvents()
    protected fun push(key: K) = setState { copy(stack = BackStackReducer.push(stack, key)) }
    protected fun pop(count: Int = 1) = setState { copy(stack = BackStackReducer.pop(stack, count)) }
    protected fun replaceTop(key: K) = setState { copy(stack = BackStackReducer.replaceTop(stack, key)) }
}
