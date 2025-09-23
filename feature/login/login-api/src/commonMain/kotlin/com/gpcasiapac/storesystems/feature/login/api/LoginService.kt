package com.gpcasiapac.storesystems.feature.login.api

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.api.LogoutResult
import com.gpcasiapac.storesystems.core.identity.api.model.AuthSession
import com.gpcasiapac.storesystems.core.identity.api.model.User

/**
 * Public, non-UI contract for Login feature. Other modules/apps can depend on this
 * to perform authentication-related operations without importing implementation.
 *
 * Note: API is decoupled from login-domain. It returns DataResult<AuthSession> on login.
 */
interface LoginService {
    suspend fun login(username: String, password: String): DataResult<AuthSession>
    suspend fun logout(): LogoutResult

    /** Retrieve the current user if authenticated */
    suspend fun getCurrentUser(): DataResult<User>

    /** Convenience helper */
    suspend fun isLoggedIn(): Boolean
}
