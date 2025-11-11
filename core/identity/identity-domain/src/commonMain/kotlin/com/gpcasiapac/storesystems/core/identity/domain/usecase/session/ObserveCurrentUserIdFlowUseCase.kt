package com.gpcasiapac.storesystems.core.identity.domain.usecase.session

import com.gpcasiapac.storesystems.core.identity.api.SessionRepository
import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
import kotlinx.coroutines.flow.Flow

class ObserveCurrentUserIdFlowUseCase(
    private val sessionRepository: SessionRepository,
) {
    operator fun invoke(): Flow<UserId?> {
        return sessionRepository.userIdFlow()
    }
}