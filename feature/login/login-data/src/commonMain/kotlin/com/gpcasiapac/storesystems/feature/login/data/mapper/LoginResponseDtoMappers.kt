package com.gpcasiapac.storesystems.feature.login.data.mapper

import com.gpcasiapac.storesystems.feature.login.data.network.dto.LoginResponseDto
import com.gpcasiapac.storesystems.feature.login.domain.repository.LoginResult

fun LoginResponseDto.toDomain(): LoginResult {
    return LoginResult(
        user = user.toDomain(),
        token = token.toDomain()
    )
}