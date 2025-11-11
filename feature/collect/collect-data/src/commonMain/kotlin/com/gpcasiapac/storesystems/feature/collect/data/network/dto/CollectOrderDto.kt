package com.gpcasiapac.storesystems.feature.collect.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollectOrderDto(

    @SerialName("id")
    val id: String,

    @SerialName("invoiceNumber")
    val invoiceNumber: String,

    @SerialName("orderNumber")
    val orderNumber: String,

    @SerialName("webOrderNumber")
    val webOrderNumber: String?,

    @SerialName("orderChannel")
    val orderChannel: Int,

    @SerialName("invoiceDateTime")
    val invoiceDateTime: String,

    @SerialName("createdDateTime")
    val createdDateTime: String,

    @SerialName("customer")
    val customer: CollectOrderCustomerDto,

    @SerialName("isLocked")
    val isLocked: Boolean,

    @SerialName("lockedBy")
    val lockedBy: String? = null,

    @SerialName("lockedDateTime")
    val lockedDateTime: String? = null,

    @SerialName("lineItems")
    val lineItems: List<CollectOrderLineItemDto> = emptyList(),

)

