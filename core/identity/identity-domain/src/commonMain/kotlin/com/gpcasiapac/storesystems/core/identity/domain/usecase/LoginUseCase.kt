package com.gpcasiapac.storesystems.core.identity.domain.usecase

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.api.model.AuthSession
import com.gpcasiapac.storesystems.core.identity.domain.repository.UserRepository
import com.gpcasiapac.storesystems.core.identity.domain.usecase.session.SetAccessTokenUseCase
import com.gpcasiapac.storesystems.core.identity.domain.usecase.session.SetUserIdUseCase

class LoginUseCase(
    private val userRepository: UserRepository,
    private val setUserIdUseCase: SetUserIdUseCase,
    private val setAccessTokenUseCase: SetAccessTokenUseCase,
) {
    suspend operator fun invoke(username: String, password: String): DataResult<AuthSession> {
        return when (val result = userRepository.login(username, password)) {
            is DataResult.Success -> {
                // Side-effect: store session via SessionManager through dedicated use cases
                val session = result.data
                setUserIdUseCase(session.user.username)
                setAccessTokenUseCase(session.token.accessToken)
                result
            }
            is DataResult.Error -> result
        }
    }
}
