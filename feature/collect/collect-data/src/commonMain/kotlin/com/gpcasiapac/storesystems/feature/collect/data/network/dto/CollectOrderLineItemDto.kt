package com.gpcasiapac.storesystems.feature.collect.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollectOrderLineItemDto(
    @SerialName("line_number")
    val lineNumber: Int,
    @SerialName("sku")
    val sku: String,
    @SerialName("product_number")
    val productNumber: String,
    @SerialName("product_description")
    val productDescription: String,
    @SerialName("quantity")
    val quantity: Int,
    @SerialName("unit_price")
    val unitPrice: Double,
    @SerialName("product_image_url")
    val productImageUrl: String? = null,
)
