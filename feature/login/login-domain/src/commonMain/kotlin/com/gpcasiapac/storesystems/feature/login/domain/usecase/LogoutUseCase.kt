package com.gpcasiapac.storesystems.feature.login.domain.usecase

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.feature.login.domain.repository.LoginRepository

class LogoutUseCase(
    private val loginRepository: LoginRepository
) {
    suspend operator fun invoke(): UseCaseResult {
        return when (val result = loginRepository.logout()) {
            is DataResult.Success -> {
                UseCaseResult.Success
            }
            is DataResult.Error.Client.Database -> {
                UseCaseResult.Error.ServiceUnavailable
            }
            is DataResult.Error.Network.ConnectionError -> {
                UseCaseResult.Error.NetworkError
            }
            is DataResult.Error.Network.HttpError -> {
                when (result.code) {
                    401 -> UseCaseResult.Success // Already logged out
                    else -> UseCaseResult.Error.ServiceUnavailable
                }
            }
            is DataResult.Error -> {
                UseCaseResult.Error.ServiceUnavailable
            }
        }
    }
    
    sealed interface UseCaseResult {
        data object Success : UseCaseResult
        
        sealed class Error(val message: String) : UseCaseResult {
            data object NetworkError : Error("Network connection error. Please check your internet connection.")
            data object ServiceUnavailable : Error("Logout service is currently unavailable. Please try again later.")
        }
    }
}