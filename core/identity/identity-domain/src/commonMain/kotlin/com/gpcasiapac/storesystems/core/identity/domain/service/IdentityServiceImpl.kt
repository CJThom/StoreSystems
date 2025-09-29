package com.gpcasiapac.storesystems.core.identity.domain.service

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.api.IdentityService
import com.gpcasiapac.storesystems.core.identity.api.LogoutResult
import com.gpcasiapac.storesystems.core.identity.api.model.AuthSession
import com.gpcasiapac.storesystems.core.identity.api.model.Token
import com.gpcasiapac.storesystems.core.identity.api.model.User
import com.gpcasiapac.storesystems.core.identity.domain.usecase.GetCurrentUserUseCase
import com.gpcasiapac.storesystems.core.identity.domain.usecase.IsLoggedInUseCase
import com.gpcasiapac.storesystems.core.identity.domain.usecase.LoginUseCase
import com.gpcasiapac.storesystems.core.identity.domain.usecase.LogoutUseCase
import com.gpcasiapac.storesystems.core.identity.domain.usecase.RefreshTokenUseCase

/**
 * IdentityService implementation now lives in identity-domain to keep impl separate from API
 * and avoid a dedicated identity-impl module. It composes domain use cases.
 */
class IdentityServiceImpl(
    private val loginUseCase: LoginUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val isLoggedInUseCase: IsLoggedInUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val refreshTokenUseCase: RefreshTokenUseCase,
) : IdentityService {

    override suspend fun login(username: String, password: String): DataResult<AuthSession> =
        loginUseCase(username, password)

    override suspend fun getCurrentUser(): DataResult<User> = getCurrentUserUseCase()

    override suspend fun isLoggedIn(): Boolean = isLoggedInUseCase()

    override suspend fun logout(): LogoutResult = when (logoutUseCase()) {
        is LogoutUseCase.UseCaseResult.Success -> LogoutResult.Success
        is LogoutUseCase.UseCaseResult.Error.NetworkError -> LogoutResult.Error.NetworkError
        is LogoutUseCase.UseCaseResult.Error.ServiceUnavailable -> LogoutResult.Error.ServiceUnavailable
    }

    override suspend fun refreshToken(refreshToken: String): DataResult<Token> =
        refreshTokenUseCase(refreshToken)
}
