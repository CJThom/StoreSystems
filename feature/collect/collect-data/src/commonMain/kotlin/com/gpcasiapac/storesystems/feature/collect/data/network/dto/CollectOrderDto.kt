package com.gpcasiapac.storesystems.feature.collect.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
 data class CollectOrderDto(

    // Legacy fields (kept for backward compatibility with existing JSON/migrations)
    @SerialName("customer_type")
    val customerType: String,

    // Legacy: some feeds still provide company/legacy name as customer_name
    @SerialName("customer_name")
    val customerName: String,

    // Preferred company/account name for B2B
    @SerialName("account_name")
    val accountName: String? = null,

    // New grouped customer fields
    @SerialName("customer_first_name")
    val customerFirstName: String? = null,

    @SerialName("customer_last_name")
    val customerLastName: String? = null,

    @SerialName("customer_phone")
    val customerPhone: String? = null,

    @SerialName("customer_number")
    val customerNumber: String,

    @SerialName("invoice_number")
    val invoiceNumber: String,

    @SerialName("sales_order_number")
    val salesOrderNumber: String,

    @SerialName("web_order_number")
    val webOrderNumber: String?,

    @SerialName("created_at_epoch_ms")
    val createdAtEpochMillis: Long,

    @SerialName("picked_at_epoch_ms")
    val pickedAtEpochMillis: Long,

)
