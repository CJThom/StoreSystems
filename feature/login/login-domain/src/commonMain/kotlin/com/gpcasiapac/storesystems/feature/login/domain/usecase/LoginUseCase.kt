package com.gpcasiapac.storesystems.feature.login.domain.usecase

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.api.IdentityService
import com.gpcasiapac.storesystems.core.identity.api.model.Token
import com.gpcasiapac.storesystems.core.identity.api.model.User

class LoginUseCase(
    private val identityService: IdentityService
) {
    suspend operator fun invoke(username: String, password: String): UseCaseResult {
        val cleanUsername = username.trim()
        val cleanPassword = password.trim()
        
        // Business logic validation
        if (cleanUsername.isEmpty()) {
            return UseCaseResult.Error.EmptyUsername
        }
        
        if (cleanPassword.isEmpty()) {
            return UseCaseResult.Error.EmptyPassword
        }
        
        if (cleanUsername.length < 3) {
            return UseCaseResult.Error.InvalidUsername
        }
        
        if (cleanPassword.length < 6) {
            return UseCaseResult.Error.InvalidPassword
        }
        
        return when (val result = identityService.login(cleanUsername, cleanPassword)) {
            is DataResult.Success -> {
                // Business logic validation
                if (result.data.user.username.isBlank() || result.data.token.accessToken.isBlank()) {
                    UseCaseResult.Error.LoginFailed
                } else {
                    UseCaseResult.Success(
                        user = result.data.user,
                        token = result.data.token
                    )
                }
            }
            is DataResult.Error.Client.Database -> {
                UseCaseResult.Error.ServiceUnavailable
            }
            is DataResult.Error.Client.Mapping -> {
                UseCaseResult.Error.LoginFailed
            }
            is DataResult.Error.Network.ConnectionError -> {
                UseCaseResult.Error.NetworkError
            }
            is DataResult.Error.Network.HttpError -> {
                when (result.code) {
                    400 -> UseCaseResult.Error.InvalidCredentials
                    401 -> UseCaseResult.Error.InvalidCredentials
                    403 -> UseCaseResult.Error.AccountLocked
                    404 -> UseCaseResult.Error.InvalidCredentials
                    429 -> UseCaseResult.Error.TooManyAttempts
                    else -> UseCaseResult.Error.ServiceUnavailable
                }
            }
            is DataResult.Error -> {
                UseCaseResult.Error.ServiceUnavailable
            }
        }
    }
    
    sealed interface UseCaseResult {
        data class Success(val user: User, val token: Token) : UseCaseResult
        
        sealed class Error(val message: String) : UseCaseResult {
            data object EmptyUsername : Error("Username cannot be empty.")
            data object EmptyPassword : Error("Password cannot be empty.")
            data object InvalidUsername : Error("Username must be at least 3 characters long.")
            data object InvalidPassword : Error("Password must be at least 6 characters long.")
            data object InvalidCredentials : Error("Invalid username or password. Please try again.")
            data object AccountLocked : Error("Account is locked. Please contact support.")
            data object TooManyAttempts : Error("Too many login attempts. Please try again later.")
            data object NetworkError : Error("Network connection error. Please check your internet connection.")
            data object LoginFailed : Error("Login failed. Please try again.")
            data object ServiceUnavailable : Error("Login service is currently unavailable. Please try again later.")
        }
    }
}