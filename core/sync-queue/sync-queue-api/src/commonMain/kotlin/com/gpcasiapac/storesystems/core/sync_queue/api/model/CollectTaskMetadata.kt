@file:OptIn(ExperimentalTime::class)

package com.gpcasiapac.storesystems.core.sync_queue.api.model

import kotlin.time.ExperimentalTime
import kotlin.time.Instant

/**
 * API model for collect task metadata.
 * Contains order and customer information for collect tasks.
 */
 data class CollectTaskMetadata(
    val id: String,
    val syncTaskId: String,

    // Order information
    val invoiceNumber: String,
    val orderNumber: String,
    val webOrderNumber: String?,
    val createdDateTime: Instant,
    val invoiceDateTime: Instant,

    // Customer information
    val customerNumber: String,
    val customerType: String, // "B2B" or "B2C"
    val name: String,
    val phone: String,
)
