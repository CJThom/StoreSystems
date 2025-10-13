package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.model.OrderSelectionResult
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection.ObserveOrderSelectionUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map

class ObserveOrderSelectionResultUseCase(
    private val observeOrderSelectionUseCase: ObserveOrderSelectionUseCase,
    private val getCollectOrderWithCustomerWithLineItemsFlowUseCase: GetCollectOrderWithCustomerWithLineItemsFlowUseCase,
    private val getCollectOrderWithCustomerListFlowUseCase: GetCollectOrderWithCustomerListFlowUseCase,
) {
    operator fun invoke(userRefId: String): Flow<OrderSelectionResult> {
        return observeOrderSelectionUseCase(userRefId).flatMapLatest { selectionSet ->
            when {
                selectionSet.size == 1 -> {
                    getCollectOrderWithCustomerWithLineItemsFlowUseCase(selectionSet.first()).map { order ->
                        OrderSelectionResult.Single(order)
                    }
                }
                else -> {
                    // When selectionSet is empty, this efficiently gets all orders.
                    // When it's populated, it gets only the selected ones.
                    getCollectOrderWithCustomerListFlowUseCase(selectionSet).map { selectedOrders ->
                        OrderSelectionResult.Multi(selectedOrders)
                    }
                }
            }
        }
    }
}
