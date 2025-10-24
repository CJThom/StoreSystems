package com.gpcasiapac.storesystems.core.identity.data.network.source

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.data.network.dto.LoginRequestDto
import com.gpcasiapac.storesystems.core.identity.data.network.dto.LoginResponseDto
import com.gpcasiapac.storesystems.core.identity.data.network.dto.TokenDto
import com.gpcasiapac.storesystems.core.identity.data.network.dto.UserDto

interface IdentityNetworkDataSource {

    suspend fun login(loginRequest: LoginRequestDto): DataResult<LoginResponseDto>

    suspend fun refreshToken(refreshToken: String): DataResult<TokenDto>

    suspend fun logout(): DataResult<Unit>

    suspend fun getCurrentUser(): DataResult<UserDto>

    suspend fun validateToken(token: String): DataResult<Boolean>

}
