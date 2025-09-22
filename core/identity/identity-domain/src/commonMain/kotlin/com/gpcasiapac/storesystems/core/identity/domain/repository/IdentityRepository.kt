package com.gpcasiapac.storesystems.core.identity.domain.repository

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.api.model.AuthSession
import com.gpcasiapac.storesystems.core.identity.api.model.Token
import com.gpcasiapac.storesystems.core.identity.api.model.User

interface IdentityRepository {
    suspend fun login(username: String, password: String): DataResult<AuthSession>
    suspend fun refreshToken(refreshToken: String): DataResult<Token>
    suspend fun logout(): DataResult<Unit>
    suspend fun getCurrentUser(): DataResult<User>
    suspend fun validateToken(token: String): DataResult<Boolean>
}
