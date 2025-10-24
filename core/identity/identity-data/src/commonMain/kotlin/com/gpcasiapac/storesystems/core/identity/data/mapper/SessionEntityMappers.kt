package com.gpcasiapac.storesystems.core.identity.data.mapper

import com.gpcasiapac.storesystems.core.identity.api.model.AuthSession
import com.gpcasiapac.storesystems.core.identity.api.model.Token
import com.gpcasiapac.storesystems.core.identity.api.model.User
import com.gpcasiapac.storesystems.core.identity.data.local.db.entity.SessionEntity
import com.gpcasiapac.storesystems.core.identity.data.local.db.entity.UserEntity

fun toAuthSessionEntity(user: User, token: Token): SessionEntity = SessionEntity(
    singleton = 1,
    userId = user.username, // Store username as the foreign key
    accessToken = token.accessToken,
    refreshToken = token.refreshToken,
    tokenType = token.tokenType,
    expiresIn = token.expiresIn,
    issuedAt = token.issuedAt,
    scope = token.scope,
)

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
