package com.gpcasiapac.storesystems.feature.history.domain.model

import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

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
    val salesOrderNumber: String,
    val webOrderNumber: String?,
    val orderCreatedAt: Instant,
    val orderPickedAt: Instant,
    
    // Collect metadata - Customer information
    val customerNumber: String,
    val customerType: String,
    val accountName: String?,
    val firstName: String?,
    val lastName: String?,
    val phone: String?
) {
    /**
     * Convert to base HistoryItem (without metadata).
     */
    fun toHistoryItem(): HistoryItem {
        return HistoryItem(
            id = id,
            type = type,
            entityId = entityId,
            status = status,
            timestamp = timestamp,
            attempts = attempts,
            lastError = lastError,
            priority = priority
        )
    }
    
    /**
     * Get customer display name based on customer type.
     */
    fun getCustomerDisplayName(): String {
        return when (customerType) {
            "B2B" -> accountName ?: customerNumber
            "B2C" -> {
                val name = listOfNotNull(firstName, lastName)
                    .filter { it.isNotBlank() }
                    .joinToString(" ")
                name.ifBlank { customerNumber }
            }
            else -> customerNumber
        }
    }
}
