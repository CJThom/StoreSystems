package com.gpcasiapac.storesystems.core.identity.data.mapper

import com.gpcasiapac.storesystems.core.identity.api.model.User
import com.gpcasiapac.storesystems.core.identity.data.network.dto.UserDto

fun UserDto.toDomain(): User = User(
    id = id,
    username = username,
    email = email,
    firstName = firstName,
    lastName = lastName,
    isActive = isActive,
    createdAt = createdAt,
    lastLoginAt = lastLoginAt,
)
