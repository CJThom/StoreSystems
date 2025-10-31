package com.gpcasiapac.storesystems.feature.collect.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CollectOrderCustomerDto(

    @SerialName("number")
    val number: String,

    @SerialName("name")
    val name: String,

    @SerialName("phone")
    val phone: String?,

    @SerialName("companyRepresentatives")
    val companyRepresentatives: List<String>? = null,

)