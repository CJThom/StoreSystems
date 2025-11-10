package com.gpcasiapac.storesystems.feature.history.domain.model

import com.gpcasiapac.storesystems.feature.history.api.HistoryType
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * Domain model representing a history item with collect task metadata.
 * Combines base history information with order and customer details.
 */
@OptIn(ExperimentalTime::class)
 data class HistoryItemWithCollectMetadata(
    // Base history fields
    val id: String,
    val type: HistoryType,
    val entityId: String,
    val status: HistoryStatus,
    val timestamp: Instant,
    val attempts: Int,
    val lastError: String?,
    val priority: Int,

    // Collect metadata - Order information
    val invoiceNumber: String,
    val orderNumber: String,
    val webOrderNumber: String?,
    val createdDateTime: Instant,
    val invoiceDateTime: Instant,

    // Collect metadata - Customer information
    val customerNumber: String,
    val customerType: String,
    val name: String,
    val phone: String,
) {
    /**
     * Get customer display name based on available fields.
     */
    fun getCustomerDisplayName(): String {
        return name.ifBlank { customerNumber }
    }
}
