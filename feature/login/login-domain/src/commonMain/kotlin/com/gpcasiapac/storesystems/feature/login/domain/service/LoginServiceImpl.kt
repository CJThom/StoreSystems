package com.gpcasiapac.storesystems.feature.login.domain.service

import com.gpcasiapac.storesystems.feature.login.api.LoginService
import com.gpcasiapac.storesystems.feature.login.domain.usecase.LoginUseCase

/**
 * LoginService implementation provided from the domain module.
 * It adapts the LoginUseCase result to the API-level DataResult<AuthSession>.
 */
class LoginServiceImpl(
    private val loginUseCase: LoginUseCase,
) : LoginService
