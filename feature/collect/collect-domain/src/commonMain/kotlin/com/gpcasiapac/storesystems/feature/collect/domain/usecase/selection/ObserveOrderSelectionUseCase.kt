package com.gpcasiapac.storesystems.feature.collect.domain.usecase.selection

import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.ObserveWorkOrderWithOrderWithCustomersUseCase
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class ObserveOrderSelectionUseCase(
    private val observeWorkOrderWithOrderWithCustomersUseCase: ObserveWorkOrderWithOrderWithCustomersUseCase,
) {

    operator fun invoke(workOrderId: WorkOrderId): Flow<Set<String>> {
        return observeWorkOrderWithOrderWithCustomersUseCase(workOrderId = workOrderId).map {  workOrderWithOrderWithCustomers ->
            workOrderWithOrderWithCustomers?.collectOrderWithCustomerList
                ?.map { collectOrderWithCustomer ->
                    collectOrderWithCustomer.order.invoiceNumber }
                ?.toSet()
                ?: emptySet()
        }
    }

}
