package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveOrderSelectionUseCase(
    private val observeWorkOrderWithOrderWithCustomersUseCase: ObserveWorkOrderWithOrderWithCustomersUseCase,
) {

    operator fun invoke(workOrderId: WorkOrderId): Flow<Set<InvoiceNumber>> {
        return observeWorkOrderWithOrderWithCustomersUseCase(workOrderId = workOrderId).map { workOrderWithOrderWithCustomers ->
            workOrderWithOrderWithCustomers?.collectOrderWithCustomerList
                ?.map { collectOrderWithCustomer ->
                    collectOrderWithCustomer.order.invoiceNumber }
                ?.toSet()
                ?: emptySet()
        }
    }

}