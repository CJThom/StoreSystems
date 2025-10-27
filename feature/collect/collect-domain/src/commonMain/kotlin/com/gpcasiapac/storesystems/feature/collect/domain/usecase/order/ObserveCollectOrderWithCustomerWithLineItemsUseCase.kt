package com.gpcasiapac.storesystems.feature.collect.domain.usecase.order

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.Flow

class ObserveCollectOrderWithCustomerWithLineItemsUseCase(
    private val orderRepository: OrderRepository,
) {

    operator fun invoke(invoiceNumber: String): Flow<CollectOrderWithCustomerWithLineItems?> {
        return orderRepository.getCollectOrderWithCustomerWithLineItemsFlow(invoiceNumber = invoiceNumber)
    }

}