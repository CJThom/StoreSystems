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
abstract class BaseNavViewModel<Event : ViewEvent,  State : ViewStateWithNavigation<State>, K : NavKey> :
    MVIViewModel<Event, State, ViewSideEffect>() {

    // Protected helpers for subclasses to update the back stack from their handleEvents()
    protected fun push(key: K) = setState {
        copyWithStack(BackStackReducer.push(stack, key))
    }

    protected fun pop(count: Int = 1) = setState {
        copyWithStack(BackStackReducer.pop(stack, count))
    }

    protected fun replaceTop(key: K) = setState {
        copyWithStack(BackStackReducer.replaceTop(stack, key))
    }

    protected fun pushOrReplaceTop(key: K) = setState {
        copyWithStack(BackStackReducer.pushOrReplace(stack, key))
    }

    protected fun truncateAfterAndPush(key: K, afterKey: K) = setState {
        copyWithStack(BackStackReducer.truncateAfterAndPush(stack, key, afterKey))
    }

}
