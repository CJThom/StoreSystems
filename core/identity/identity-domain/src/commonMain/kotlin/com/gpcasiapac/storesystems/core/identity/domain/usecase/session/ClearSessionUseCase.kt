package com.gpcasiapac.storesystems.core.identity.domain.usecase.session

import com.gpcasiapac.storesystems.core.identity.api.SessionManager

class ClearSessionUseCase(
    private val sessionManager: SessionManager,
) {
    suspend operator fun invoke() = sessionManager.clear()
}