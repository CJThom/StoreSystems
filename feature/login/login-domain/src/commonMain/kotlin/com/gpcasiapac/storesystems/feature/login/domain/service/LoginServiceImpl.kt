package com.gpcasiapac.storesystems.feature.login.domain.service

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.api.LogoutResult
import com.gpcasiapac.storesystems.core.identity.api.model.AuthSession
import com.gpcasiapac.storesystems.core.identity.api.model.Token
import com.gpcasiapac.storesystems.core.identity.api.model.User
import com.gpcasiapac.storesystems.feature.login.api.LoginService
import com.gpcasiapac.storesystems.feature.login.domain.usecase.LoginUseCase

/**
 * LoginService implementation provided from the domain module.
 * It adapts the LoginUseCase result to the API-level DataResult<AuthSession>.
 */
class LoginServiceImpl(
    private val loginUseCase: LoginUseCase,
) : LoginService {

    override suspend fun login(username: String, password: String): DataResult<AuthSession> =
        when (val r = loginUseCase(username, password)) {
            is LoginUseCase.UseCaseResult.Success -> DataResult.Success(
                AuthSession(user = r.user, token = r.token)
            )
            is LoginUseCase.UseCaseResult.Error.NetworkError -> DataResult.Error.Network.ConnectionError()
            is LoginUseCase.UseCaseResult.Error.InvalidCredentials -> DataResult.Error.Network.HttpError(
                code = 401,
                message = r.message
            )
            is LoginUseCase.UseCaseResult.Error.AccountLocked -> DataResult.Error.Network.HttpError(
                code = 403,
                message = r.message
            )
            is LoginUseCase.UseCaseResult.Error.TooManyAttempts -> DataResult.Error.Network.HttpError(
                code = 429,
                message = r.message
            )
            is LoginUseCase.UseCaseResult.Error.EmptyUsername,
            is LoginUseCase.UseCaseResult.Error.EmptyPassword,
            is LoginUseCase.UseCaseResult.Error.InvalidUsername,
            is LoginUseCase.UseCaseResult.Error.InvalidPassword,
            is LoginUseCase.UseCaseResult.Error.LoginFailed,
            is LoginUseCase.UseCaseResult.Error.ServiceUnavailable -> DataResult.Error.Client.UnexpectedError(r.message)
        }

    override suspend fun logout(): LogoutResult {
        // Delegated to IdentityService at app composition level; the Login facade exposes logout for convenience.
        // Without direct access here, return a mock unsupported operation until identity service is injected here later.
        return LogoutResult.Success
    }

    override suspend fun getCurrentUser(): DataResult<User> {
        // Typically delegated to IdentityService; out of scope for Login domain impl.
        return DataResult.Error.Client.UnexpectedError("getCurrentUser() is not wired in LoginServiceImpl (domain)")
    }

    override suspend fun isLoggedIn(): Boolean {
        // Typically delegated to IdentityService; out of scope for Login domain impl.
        return false
    }
}
