package com.gpcasiapac.storesystems.core.identity.domain.usecase.session

import com.gpcasiapac.storesystems.core.identity.api.SessionRepository

class ClearSessionUseCase(
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke() = sessionRepository.clear()
}