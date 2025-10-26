package com.gpcasiapac.storesystems.core.identity.domain.usecase.session

import com.gpcasiapac.storesystems.core.identity.api.SessionRepository

class SetAccessTokenUseCase(
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(token: String) = sessionRepository.setAccessToken(token)
}