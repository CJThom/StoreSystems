package com.gpcasiapac.storesystems.core.identity.data.mapper

import com.gpcasiapac.storesystems.core.identity.api.model.User
import com.gpcasiapac.storesystems.core.identity.data.local.db.entity.UserEntity

fun User.toEntity(): UserEntity = UserEntity(
    username = username,
    email = email,
    firstName = firstName,
    lastName = lastName,
    isActive = isActive,
    createdAt = createdAt,
    lastLoginAt = lastLoginAt,
)

