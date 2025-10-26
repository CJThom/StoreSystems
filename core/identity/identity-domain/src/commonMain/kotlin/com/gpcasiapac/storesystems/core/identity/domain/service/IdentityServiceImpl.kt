package com.gpcasiapac.storesystems.core.identity.domain.service

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.api.IdentityService
import com.gpcasiapac.storesystems.core.identity.api.model.AuthSession
import com.gpcasiapac.storesystems.core.identity.api.model.User
import com.gpcasiapac.storesystems.core.identity.domain.usecase.GetUserUseCase
import com.gpcasiapac.storesystems.core.identity.domain.usecase.IsLoggedInUseCase
import com.gpcasiapac.storesystems.core.identity.domain.usecase.LoginUseCase
import com.gpcasiapac.storesystems.core.identity.domain.usecase.LogoutUseCase

/**
 * IdentityService implementation now lives in identity-domain to keep impl separate from API
 * and avoid a dedicated identity-impl module. It composes domain use cases.
 */
class IdentityServiceImpl(
    private val loginUseCase: LoginUseCase,
    private val getUserUseCase: GetUserUseCase,
    private val isLoggedInUseCase: IsLoggedInUseCase,
    private val logoutUseCase: LogoutUseCase,
) : IdentityService {

    override suspend fun login(username: String, password: String): DataResult<AuthSession> {
        // All session side-effects are handled inside LoginUseCase
        return loginUseCase(username, password)
    }

    override suspend fun getUser(userId: String): User? {
        return getUserUseCase(userId)
    }

    override suspend fun isLoggedIn(): Boolean {
        return isLoggedInUseCase()
    }

    override suspend fun logout() {
        logoutUseCase()
    }

}
