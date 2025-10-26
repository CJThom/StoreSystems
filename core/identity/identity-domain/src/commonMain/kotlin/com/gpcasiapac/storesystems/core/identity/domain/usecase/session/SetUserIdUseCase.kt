package com.gpcasiapac.storesystems.core.identity.domain.usecase.session

import com.gpcasiapac.storesystems.core.identity.api.SessionManager

class SetUserIdUseCase(
    private val sessionManager: SessionManager,
) {
    suspend operator fun invoke(userId: String) = sessionManager.setUserId(userId)
}