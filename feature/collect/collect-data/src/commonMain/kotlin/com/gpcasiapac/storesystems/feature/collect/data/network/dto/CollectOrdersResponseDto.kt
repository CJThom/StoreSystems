package com.gpcasiapac.storesystems.feature.collect.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollectOrdersResponseDto(

    @SerialName("collectOrders")
    val collectOrders: List<CollectOrderDto>

)