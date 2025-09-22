package com.gpcasiapac.storesystems.core.identity.domain.usecase

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.api.model.Token
import com.gpcasiapac.storesystems.core.identity.domain.repository.IdentityRepository

class RefreshTokenUseCase(
    private val identityRepository: IdentityRepository
) {
    suspend operator fun invoke(refreshToken: String): DataResult<Token> =
        identityRepository.refreshToken(refreshToken)
}
