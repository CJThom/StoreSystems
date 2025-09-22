package com.gpcasiapac.storesystems.core.identity.data.mapper

import com.gpcasiapac.storesystems.core.identity.api.model.Token
import com.gpcasiapac.storesystems.core.identity.data.network.dto.TokenDto

fun TokenDto.toDomain(): Token = Token(
    accessToken = accessToken,
    refreshToken = refreshToken,
    tokenType = tokenType,
    expiresIn = expiresIn,
    issuedAt = issuedAt,
    scope = scope,
)
