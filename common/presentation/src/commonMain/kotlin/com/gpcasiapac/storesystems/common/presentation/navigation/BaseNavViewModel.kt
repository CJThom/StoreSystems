package com.gpcasiapac.storesystems.common.presentation.navigation

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
abstract class BaseNavViewModel<K : FeatureKey> :
    MVIViewModel<NavEvent<K>, NavState<K>, ViewSideEffect>() {

    /**
     * Provide the starting key shown as the root of the back stack.
     * Using a function avoids subclass property initialization order issues during base init.
     */
    protected abstract fun provideStartKey(): K

    override fun setInitialState(): NavState<K> = NavState(stack = listOf(provideStartKey()))

    override fun onStart() { /* no-op by default */ }

    override fun handleEvents(event: NavEvent<K>) {
        when (event) {
            is NavEvent.Push -> setState { copy(stack = BackStackReducer.push(stack, event.key)) }
            is NavEvent.Pop -> setState { copy(stack = BackStackReducer.pop(stack, event.count)) }
            is NavEvent.Replace -> setState { copy(stack = BackStackReducer.replaceTop(stack, event.key)) }
        }
    }
}
