package com.gpcasiapac.storesystems.feature.login.data.mapper

import com.gpcasiapac.storesystems.feature.login.data.network.dto.UserDto
import com.gpcasiapac.storesystems.feature.login.domain.model.User

fun User.toDto(): UserDto {
    return UserDto(
        id = id,
        username = username,
        email = email,
        firstName = firstName,
        lastName = lastName,
        isActive = isActive,
        createdAt = createdAt,
        lastLoginAt = lastLoginAt
    )
}