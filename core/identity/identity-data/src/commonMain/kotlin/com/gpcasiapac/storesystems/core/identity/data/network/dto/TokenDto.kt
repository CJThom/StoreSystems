package com.gpcasiapac.storesystems.core.identity.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenDto(

    @SerialName("access_token")
    val accessToken: String,

    @SerialName("refresh_token")
    val refreshToken: String,

    @SerialName("token_type")
    val tokenType: String = "Bearer",

    @SerialName("expires_in")
    val expiresIn: Long,

    @SerialName("issued_at")
    val issuedAt: Long,

    @SerialName("scope")
    val scope: String? = null,

)
