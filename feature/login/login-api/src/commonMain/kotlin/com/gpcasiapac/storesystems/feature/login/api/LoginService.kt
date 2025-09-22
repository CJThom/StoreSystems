package com.gpcasiapac.storesystems.feature.login.api

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.api.model.User
import com.gpcasiapac.storesystems.feature.login.domain.usecase.LoginUseCase
import com.gpcasiapac.storesystems.core.identity.api.LogoutResult

/**
 * Public, non-UI contract for Login feature. Other modules/apps can depend on this
 * to perform authentication-related operations without importing implementation.
 */
interface LoginService {
    suspend fun login(username: String, password: String): LoginUseCase.UseCaseResult
    suspend fun logout(): LogoutResult

    /** Retrieve the current user if authenticated */
    suspend fun getCurrentUser(): DataResult<User>

    /** Convenience helper */
    suspend fun isLoggedIn(): Boolean
}
