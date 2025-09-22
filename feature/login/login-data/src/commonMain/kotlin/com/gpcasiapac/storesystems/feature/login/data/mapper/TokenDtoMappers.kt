package com.gpcasiapac.storesystems.feature.login.data.mapper

import com.gpcasiapac.storesystems.feature.login.data.network.dto.TokenDto
import com.gpcasiapac.storesystems.feature.login.domain.model.Token

fun TokenDto.toDomain(): Token {
    return Token(
        accessToken = accessToken,
        refreshToken = refreshToken,
        tokenType = tokenType,
        expiresIn = expiresIn,
        issuedAt = issuedAt,
        scope = scope
    )
}