package com.gpcasiapac.storesystems.core.identity.domain.usecase.session

import com.gpcasiapac.storesystems.core.identity.api.SessionRepository
import kotlinx.coroutines.flow.Flow

class ObserveUserIdFlowUseCase(
    private val sessionRepository: SessionRepository,
) {
    operator fun invoke(): Flow<String?> = sessionRepository.userIdFlow()
}