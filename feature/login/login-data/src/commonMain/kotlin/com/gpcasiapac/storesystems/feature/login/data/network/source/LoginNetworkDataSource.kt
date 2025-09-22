package com.gpcasiapac.storesystems.feature.login.data.network.source

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.feature.login.data.network.dto.LoginRequestDto
import com.gpcasiapac.storesystems.feature.login.data.network.dto.LoginResponseDto
import com.gpcasiapac.storesystems.feature.login.data.network.dto.TokenDto
import com.gpcasiapac.storesystems.feature.login.data.network.dto.UserDto

interface LoginNetworkDataSource {

    suspend fun login(loginRequest: LoginRequestDto): DataResult<LoginResponseDto>

    suspend fun refreshToken(refreshToken: String): DataResult<TokenDto>

    suspend fun logout(): DataResult<Unit>

    suspend fun getCurrentUser(): DataResult<UserDto>

    suspend fun validateToken(token: String): DataResult<Boolean>

}
