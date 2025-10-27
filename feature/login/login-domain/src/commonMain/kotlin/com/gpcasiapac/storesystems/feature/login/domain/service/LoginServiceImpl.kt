package com.gpcasiapac.storesystems.feature.login.domain.service

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.api.model.AuthSession
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
            is LoginUseCase.UseCaseResult.Success -> {
                // Include MFA requirement in metadata
                val session = AuthSession(
                    user = r.user,
                    token = r.token,
                    metadata = mapOf(
                        "mfaRequired" to r.mfaRequired,
                        "mfaVersion" to (r.mfaVersion ?: "")
                    )
                )
                DataResult.Success(session)
            }
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

}
