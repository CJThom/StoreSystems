package com.gpcasiapac.storesystems.core.identity.api

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.api.model.AuthSession
import com.gpcasiapac.storesystems.core.identity.api.model.Token
import com.gpcasiapac.storesystems.core.identity.api.model.User

/**
 * Public facade for identity operations. Implemented in identity-impl using domain use cases.
 */
interface IdentityService {
    suspend fun login(username: String, password: String): DataResult<AuthSession>
    suspend fun getCurrentUser(): DataResult<User>
    suspend fun isLoggedIn(): Boolean
    suspend fun logout(): LogoutResult
    suspend fun refreshToken(refreshToken: String): DataResult<Token>
}

sealed interface LogoutResult {
    data object Success : LogoutResult
    sealed class Error(val message: String) : LogoutResult {
        data object NetworkError : Error("Network error. Please try again.")
        data object ServiceUnavailable : Error("Logout service unavailable. Please try again later.")
    }
}
