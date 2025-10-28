package com.gpcasiapac.storesystems.core.identity.domain.usecase

import com.gpcasiapac.storesystems.core.identity.api.SessionRepository
import kotlinx.coroutines.flow.first

class IsLoggedInUseCase(
    private val sessionRepository: SessionRepository,
) {
    suspend operator fun invoke(): Boolean {
        val userId = sessionRepository.userIdFlow().first()
        val token = sessionRepository.accessTokenFlow().first()
        return (userId != null) && !token.isNullOrBlank()
    }
}
