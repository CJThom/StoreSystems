package com.gpcasiapac.storesystems.core.identity.data.mapper

import com.gpcasiapac.storesystems.core.identity.api.model.AuthSession
import com.gpcasiapac.storesystems.core.identity.api.model.Token
import com.gpcasiapac.storesystems.core.identity.api.model.User
import com.gpcasiapac.storesystems.core.identity.data.local.db.entity.SessionEntity
import com.gpcasiapac.storesystems.core.identity.data.local.db.entity.UserEntity



fun SessionEntity.toDomain(user: UserEntity): AuthSession = AuthSession(
    user = user.toDomain(),
    token = Token(
        accessToken = accessToken,
        refreshToken = refreshToken,
        tokenType = tokenType,
        expiresIn = expiresIn,
        issuedAt = issuedAt,
        scope = scope,
    )
)
