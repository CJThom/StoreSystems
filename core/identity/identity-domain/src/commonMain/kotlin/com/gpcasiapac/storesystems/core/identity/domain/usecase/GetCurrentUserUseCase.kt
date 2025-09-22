package com.gpcasiapac.storesystems.core.identity.domain.usecase

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.api.model.User
import com.gpcasiapac.storesystems.core.identity.domain.repository.IdentityRepository

class GetCurrentUserUseCase(
    private val identityRepository: IdentityRepository
) {
    suspend operator fun invoke(): DataResult<User> = identityRepository.getCurrentUser()
}
