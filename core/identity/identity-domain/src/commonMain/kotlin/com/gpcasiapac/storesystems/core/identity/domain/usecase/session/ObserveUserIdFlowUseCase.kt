package com.gpcasiapac.storesystems.core.identity.domain.usecase.session

import com.gpcasiapac.storesystems.core.identity.api.SessionManager
import kotlinx.coroutines.flow.Flow

class ObserveUserIdFlowUseCase(
    private val sessionManager: SessionManager,
) {
    operator fun invoke(): Flow<String?> = sessionManager.userIdFlow()
}