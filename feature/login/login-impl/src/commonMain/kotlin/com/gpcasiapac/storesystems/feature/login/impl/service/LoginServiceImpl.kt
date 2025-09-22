package com.gpcasiapac.storesystems.feature.login.impl.service

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.feature.login.api.LoginService
import com.gpcasiapac.storesystems.feature.login.domain.model.User
import com.gpcasiapac.storesystems.feature.login.domain.usecase.GetCurrentUserUseCase
import com.gpcasiapac.storesystems.feature.login.domain.usecase.IsLoggedInUseCase
import com.gpcasiapac.storesystems.feature.login.domain.usecase.LoginUseCase
import com.gpcasiapac.storesystems.feature.login.domain.usecase.LogoutUseCase

class LoginServiceImpl(
    private val loginUseCase: LoginUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val isLoggedInUseCase: IsLoggedInUseCase,
) : LoginService {

    override suspend fun login(username: String, password: String): LoginUseCase.UseCaseResult {
        return loginUseCase(username, password)
    }

    override suspend fun logout(): LogoutUseCase.UseCaseResult {
        return logoutUseCase()
    }

    override suspend fun getCurrentUser(): DataResult<User> {
        return getCurrentUserUseCase()
    }

    override suspend fun isLoggedIn(): Boolean {
        return isLoggedInUseCase()
    }
}
