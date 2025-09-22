package com.gpcasiapac.storesystems.feature.login.impl.service

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.api.IdentityService
import com.gpcasiapac.storesystems.core.identity.api.LogoutResult
import com.gpcasiapac.storesystems.core.identity.api.model.User
import com.gpcasiapac.storesystems.feature.login.api.LoginService
import com.gpcasiapac.storesystems.feature.login.domain.usecase.LoginUseCase

class LoginServiceImpl(
    private val loginUseCase: LoginUseCase,
    private val identityService: IdentityService,
) : LoginService {

    override suspend fun login(username: String, password: String): LoginUseCase.UseCaseResult {
        return loginUseCase(username, password)
    }

    override suspend fun logout(): LogoutResult {
        return identityService.logout()
    }

    override suspend fun getCurrentUser(): DataResult<User> {
        return identityService.getCurrentUser()
    }

    override suspend fun isLoggedIn(): Boolean {
        return identityService.isLoggedIn()
    }
}
