package com.gpcasiapac.storesystems.feature.login.domain.repository

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.feature.login.domain.model.Token
import com.gpcasiapac.storesystems.feature.login.domain.model.User

interface LoginRepository {
    suspend fun login(username: String, password: String): DataResult<LoginResult>
    suspend fun refreshToken(refreshToken: String): DataResult<Token>
    suspend fun logout(): DataResult<Unit>
    suspend fun getCurrentUser(): DataResult<User>
    suspend fun validateToken(token: String): DataResult<Boolean>
}

data class LoginResult(
    val user: User,
    val token: Token
)