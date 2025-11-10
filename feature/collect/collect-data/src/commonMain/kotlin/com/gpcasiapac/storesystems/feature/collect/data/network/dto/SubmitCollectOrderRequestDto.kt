package com.gpcasiapac.storesystems.feature.collect.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SubmitCollectOrderRequestDto(
    @SerialName("id")
    val id: String,
    @SerialName("orderChannel")
    val orderChannel: OrderChannelDto,
    @SerialName("customerSignature")
    val customerSignature: CustomerSignatureDto,
    @SerialName("courierName")
    val courierName: String,
    @SerialName("submitTimestamp")
    val submitTimestamp: String,
    @SerialName("repID")
    val repId: String,
    @SerialName("invoices")
    val invoices: List<String>
)

@Serializable
enum class OrderChannelDto {
    @SerialName("B2B") B2B,
    @SerialName("B2C") B2C
}

@Serializable
data class CustomerSignatureDto(
    @SerialName("signature")
    val signature: String,
    @SerialName("name")
    val name: String,
    @SerialName("signatureAt")
    val signatureAt: String
)
