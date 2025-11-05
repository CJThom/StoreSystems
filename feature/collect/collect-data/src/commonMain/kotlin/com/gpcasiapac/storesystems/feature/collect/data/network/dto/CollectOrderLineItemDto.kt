package com.gpcasiapac.storesystems.feature.collect.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollectOrderLineItemDto(

    @SerialName("lineNumber")
    val lineNumber: String,

    @SerialName("sku")
    val sku: String,

    @SerialName("barcode")
    val barcode: String? = null,

    @SerialName("description")
    val description: String,

    @SerialName("quantity")
    val quantity: Int,

    @SerialName("imageUrl")
    val imageUrl: String? = null,

)
