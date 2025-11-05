package com.gpcasiapac.storesystems.feature.collect.domain.model

import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId

/**
 * Domain model representing a single item (invoice) added to a Work Order.
 * Position is 1-based and preserves the scan/insertion order within the work order.
 */
data class CollectWorkOrderItem(
    val workOrderId: WorkOrderId,
    val invoiceNumber: InvoiceNumber,
    val position: Long,
)
