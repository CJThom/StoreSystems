package com.gpcasiapac.storesystems.core.identity.domain.usecase.session

import com.gpcasiapac.storesystems.core.identity.api.SessionManager

class SetAccessTokenUseCase(
    private val sessionManager: SessionManager,
) {
    suspend operator fun invoke(token: String) = sessionManager.setAccessToken(token)
}