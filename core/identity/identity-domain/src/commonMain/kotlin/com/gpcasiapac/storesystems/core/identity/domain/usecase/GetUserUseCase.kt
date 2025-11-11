package com.gpcasiapac.storesystems.core.identity.domain.usecase

import com.gpcasiapac.storesystems.core.identity.api.model.User
import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
import com.gpcasiapac.storesystems.core.identity.domain.repository.UserRepository

class GetUserUseCase(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(userId: UserId): User? {
        return userRepository.getUser(userId)
    }
}
