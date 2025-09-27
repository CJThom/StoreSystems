package com.gpcasiapac.storesystems.common.presentation.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged

/**
 * Controls debounce timing for typing interactions.
 */
data class SearchDebounce(val millis: Long = 150L)

/**
 * Centralized, reusable shaping for search/filter inputs.
 * Keeps interaction concerns (debounce/dedupe) in presentation;
 * keeps use cases pure and reusable.
 */
object QueryFlow {

    fun <Q> build(
        input: Flow<Q>,
        debounce: SearchDebounce = SearchDebounce(),
        keySelector: (Q) -> Any? = { it },
    ): Flow<Q> = input
        .distinctUntilChanged { old, new -> keySelector(old) == keySelector(new) }
        .debounce(debounce.millis) // TODO: debug this

}
