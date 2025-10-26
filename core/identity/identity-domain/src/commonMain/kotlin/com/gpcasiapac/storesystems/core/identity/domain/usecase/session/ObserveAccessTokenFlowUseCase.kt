package com.gpcasiapac.storesystems.core.identity.domain.usecase.session

import com.gpcasiapac.storesystems.core.identity.api.SessionManager
import kotlinx.coroutines.flow.Flow

class ObserveAccessTokenFlowUseCase(
    private val sessionManager: SessionManager,
) {
    operator fun invoke(): Flow<String?> = sessionManager.accessTokenFlow()
}