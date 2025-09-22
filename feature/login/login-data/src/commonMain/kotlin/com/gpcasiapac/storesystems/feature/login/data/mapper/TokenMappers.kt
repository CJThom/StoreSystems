package com.gpcasiapac.storesystems.feature.login.data.mapper

import com.gpcasiapac.storesystems.feature.login.data.network.dto.TokenDto
import com.gpcasiapac.storesystems.feature.login.domain.model.Token

fun Token.toDto(): TokenDto {
    return TokenDto(
        accessToken = accessToken,
        refreshToken = refreshToken,
        tokenType = tokenType,
        expiresIn = expiresIn,
        issuedAt = issuedAt,
        scope = scope
    )
}
