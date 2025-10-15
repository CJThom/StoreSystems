package com.gpcasiapac.storesystems.common.presentation.flow

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

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


// todo remove example
//private suspend fun observeOrderList() {
//
//    val queryFlow: Flow<OrderQuery> = QueryFlow.build(
//        input = viewState.map { viewState ->
//            OrderQuery(viewState.searchText)
//        },
//        debounce = SearchDebounce(millis = 150),
//        keySelector = { query ->
//            query.searchText
//        }
//    )
//
//    // TODO: Query stuff
//    queryFlow.flatMapLatest { query ->
//        getCollectOrderWithCustomerListFlowUseCase()
//    }.collectLatest { orders ->
//        val collectOrderStateList = orders.toListItemState()
//        setState {
//            val newStateBase = copy(
//                collectOrderListItemStateList = collectOrderStateList,
//                orderCount = orders.size,
//                isLoading = false,
//                error = null,
//            )
//            val filtered = applyFiltersTo(collectOrderStateList, newStateBase)
//            // Maintain selection consistency when list changes
//            val filteredIds = filtered.map { it.invoiceNumber }.toSet()
//            val newSelected =
//                if (isMultiSelectionEnabled) selectedOrderIdList.intersect(filteredIds) else emptySet()
//            val allSelected =
//                isMultiSelectionEnabled && filteredIds.isNotEmpty() && filteredIds.size == newSelected.size
//            newStateBase.copy(
//                filteredCollectOrderListItemStateList = filtered,
//                selectedOrderIdList = newSelected,
//                isSelectAllChecked = allSelected
//            )
//        }
//    }
//
//}