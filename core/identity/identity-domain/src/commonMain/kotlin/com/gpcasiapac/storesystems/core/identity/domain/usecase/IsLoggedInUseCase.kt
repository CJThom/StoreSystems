package com.gpcasiapac.storesystems.core.identity.domain.usecase

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.domain.repository.IdentityRepository

class IsLoggedInUseCase(
    private val identityRepository: IdentityRepository
) {
    suspend operator fun invoke(): Boolean = when (identityRepository.getCurrentUser()) {
        is DataResult.Success -> true
        is DataResult.Error -> false
    }
}
