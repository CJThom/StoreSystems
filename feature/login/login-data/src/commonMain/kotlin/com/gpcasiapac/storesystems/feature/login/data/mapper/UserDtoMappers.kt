package com.gpcasiapac.storesystems.feature.login.data.mapper

import com.gpcasiapac.storesystems.feature.login.data.network.dto.UserDto
import com.gpcasiapac.storesystems.core.identity.api.model.User

fun UserDto.toDomain(): User {
    return User(
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