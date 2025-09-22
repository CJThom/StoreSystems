package com.gpcasiapac.storesystems.core.identity.domain.usecase

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.api.model.AuthSession
import com.gpcasiapac.storesystems.core.identity.domain.repository.IdentityRepository

class LoginUseCase(
    private val identityRepository: IdentityRepository
) {
    suspend operator fun invoke(username: String, password: String): DataResult<AuthSession> =
        identityRepository.login(username, password)
}
