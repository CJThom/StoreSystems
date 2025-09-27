package com.gpcasiapac.storesystems.feature.collect.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
 data class OrderDto(

    @SerialName("id")
    val id: String,

    @SerialName("customer_type")
    val customerType: String,

    @SerialName("customer_name")
    val customerName: String,

    @SerialName("invoice_number")
    val invoiceNumber: String,

    @SerialName("web_order_number")
    val webOrderNumber: String?,

    @SerialName("picked_at_epoch_ms")
    val pickedAtEpochMillis: Long,

)
