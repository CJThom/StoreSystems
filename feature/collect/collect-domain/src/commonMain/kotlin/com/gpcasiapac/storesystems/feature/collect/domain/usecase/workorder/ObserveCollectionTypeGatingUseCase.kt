package com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectionTypeGating
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.util.deriveCollectionTypeGating
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map

class ObserveCollectionTypeGatingUseCase(
    private val observeWorkOrderItemsInScanOrderUseCase: ObserveWorkOrderItemsInScanOrderUseCase,
) {
    operator fun invoke(workOrderId: WorkOrderId): Flow<CollectionTypeGating> =
        observeWorkOrderItemsInScanOrderUseCase(workOrderId)
            .map { orders -> deriveCollectionTypeGating(orders) }
            .distinctUntilChanged()
}