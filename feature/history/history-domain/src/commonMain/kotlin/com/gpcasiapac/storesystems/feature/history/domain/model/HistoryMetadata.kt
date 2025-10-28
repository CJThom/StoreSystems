package com.gpcasiapac.storesystems.feature.history.domain.model

import kotlinx.datetime.Instant
import kotlin.time.ExperimentalTime

/**
 * Sealed interface representing different types of metadata that can be attached to a history item.
 * This allows type-safe handling of different metadata types in the UI.
 */
sealed interface HistoryMetadata {
    
    /**
     * Metadata for collect/order submission tasks.
     * Contains order and customer information.
     */
    @OptIn(ExperimentalTime::class)
    data class CollectMetadata(
        // Order information
        val invoiceNumber: String,
        val salesOrderNumber: String,
        val webOrderNumber: String?,
        val orderCreatedAt: Instant,
        val orderPickedAt: Instant,
        
        // Customer information
        val customerNumber: String,
        val customerType: String,
        val accountName: String?,
        val firstName: String?,
        val lastName: String?,
        val phone: String?
    ) : HistoryMetadata {
        
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
    
    /**
     * No metadata attached to this history item.
     * Used for tasks that don't have additional metadata.
     */
    data object NoMetadata : HistoryMetadata
    
    // Future metadata types can be added here:
    // data class PaymentMetadata(...) : HistoryMetadata
    // data class InventoryMetadata(...) : HistoryMetadata
}
