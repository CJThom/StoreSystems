package com.gpcasiapac.storesystems.feature.login.data.network.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserDto(

    @SerialName("id")
    val id: String,

    @SerialName("username")
    val username: String,

    @SerialName("email")
    val email: String,

    @SerialName("first_name")
    val firstName: String? = null,

    @SerialName("last_name")
    val lastName: String? = null,

    @SerialName("is_active")
    val isActive: Boolean = true,

    @SerialName("created_at")
    val createdAt: Long,

    @SerialName("last_login_at")
    val lastLoginAt: Long? = null

)