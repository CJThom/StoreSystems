package com.gpcasiapac.storesystems.core.identity.domain.usecase

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.domain.repository.IdentityRepository

class LogoutUseCase(
    private val identityRepository: IdentityRepository
) {
    suspend operator fun invoke(): UseCaseResult = when (identityRepository.logout()) {
        is DataResult.Success -> UseCaseResult.Success
        is DataResult.Error.Network.ConnectionError -> UseCaseResult.Error.NetworkError
        is DataResult.Error -> UseCaseResult.Error.ServiceUnavailable
    }

    sealed interface UseCaseResult {
        data object Success : UseCaseResult
        sealed class Error(val message: String) : UseCaseResult {
            data object NetworkError : Error("Network error. Please try again.")
            data object ServiceUnavailable : Error("Logout service unavailable. Please try again later.")
        }
    }
}
