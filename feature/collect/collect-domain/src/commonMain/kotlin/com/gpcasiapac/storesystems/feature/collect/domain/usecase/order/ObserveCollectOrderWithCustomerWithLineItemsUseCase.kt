package com.gpcasiapac.storesystems.feature.collect.domain.usecase.order

import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderLocalRepository
import kotlinx.coroutines.flow.Flow

class ObserveCollectOrderWithCustomerWithLineItemsUseCase(
    private val orderLocalRepository: OrderLocalRepository,
) {

    operator fun invoke(invoiceNumber: InvoiceNumber): Flow<CollectOrderWithCustomerWithLineItems?> {
        return orderLocalRepository.getCollectOrderWithCustomerWithLineItemsFlow(invoiceNumber = invoiceNumber)
    }

}