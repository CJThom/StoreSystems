package com.gpcasiapac.storesystems.feature.history.domain.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

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
        val orderNumber: String,
        val webOrderNumber: String?,
        val createdDateTime: Instant,
        val invoiceDateTime: Instant,

        // Customer information
        val customerNumber: String,
        val customerType: String,
        val name: String,
        val phone: String,
    ) : HistoryMetadata {

        /**
         * Get customer display name based on available fields.
         */
        fun getCustomerDisplayName(): String {
            return name.ifBlank { customerNumber }
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
