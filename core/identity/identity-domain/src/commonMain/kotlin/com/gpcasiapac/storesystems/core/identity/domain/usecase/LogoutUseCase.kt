package com.gpcasiapac.storesystems.core.identity.domain.usecase

import com.gpcasiapac.storesystems.core.identity.domain.usecase.session.ClearSessionUseCase

class LogoutUseCase(
    private val clearSessionUseCase: ClearSessionUseCase,
) {
    suspend operator fun invoke() {
        clearSessionUseCase()
    }

}
