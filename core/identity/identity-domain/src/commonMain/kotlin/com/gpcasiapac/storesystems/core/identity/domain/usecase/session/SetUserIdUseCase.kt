package com.gpcasiapac.storesystems.core.identity.domain.usecase.session

import com.gpcasiapac.storesystems.core.identity.api.SessionRepository

class SetUserIdUseCase(
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(userId: String) = sessionRepository.setUserId(userId)
}