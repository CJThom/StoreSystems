package com.gpcasiapac.storesystems.core.identity.impl

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.api.IdentityService
import com.gpcasiapac.storesystems.core.identity.api.LogoutResult
import com.gpcasiapac.storesystems.core.identity.api.model.AuthSession
import com.gpcasiapac.storesystems.core.identity.api.model.Token
import com.gpcasiapac.storesystems.core.identity.api.model.User
import com.gpcasiapac.storesystems.core.identity.domain.usecase.GetCurrentUserUseCase
import com.gpcasiapac.storesystems.core.identity.domain.usecase.IsLoggedInUseCase
import com.gpcasiapac.storesystems.core.identity.domain.usecase.LogoutUseCase
import com.gpcasiapac.storesystems.core.identity.domain.usecase.RefreshTokenUseCase
import com.gpcasiapac.storesystems.core.identity.domain.usecase.LoginUseCase

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

    override suspend fun logout(): LogoutResult = when (val r = logoutUseCase()) {
        is LogoutUseCase.UseCaseResult.Success -> LogoutResult.Success
        is LogoutUseCase.UseCaseResult.Error.NetworkError -> LogoutResult.Error.NetworkError
        is LogoutUseCase.UseCaseResult.Error.ServiceUnavailable -> LogoutResult.Error.ServiceUnavailable
    }

    override suspend fun refreshToken(refreshToken: String): DataResult<Token> =
        refreshTokenUseCase(refreshToken)
}
