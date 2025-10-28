package com.gpcasiapac.storesystems.core.identity.domain.usecase.session

import com.gpcasiapac.storesystems.core.identity.api.SessionRepository
import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId

class SetUserIdUseCase(
    private val sessionRepository: SessionRepository,
) {

    suspend operator fun invoke(userId: UserId) {
        return sessionRepository.setUserId(userId)
    }

}