package com.gpcasiapac.storesystems.feature.login.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(

    @SerialName("user")
    val user: UserDto,

    @SerialName("token")
    val token: TokenDto

)