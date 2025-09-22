package com.gpcasiapac.storesystems.feature.login.data.network.source

import kotlinx.coroutines.delay
import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.feature.login.data.network.dto.LoginRequestDto
import com.gpcasiapac.storesystems.feature.login.data.network.dto.LoginResponseDto
import com.gpcasiapac.storesystems.feature.login.data.network.dto.TokenDto
import com.gpcasiapac.storesystems.feature.login.data.network.dto.UserDto

class MockLoginNetworkDataSourceImpl : LoginNetworkDataSource {
    
    private var currentUser: UserDto? = null
    private var currentToken: TokenDto? = null
    
    override suspend fun login(loginRequest: LoginRequestDto): DataResult<LoginResponseDto> {
        delay(1000) // Simulate network delay
        
        // Mock validation - accept any non-empty credentials
        if (loginRequest.username.isBlank() || loginRequest.password.isBlank()) {
            return DataResult.Error.Network.HttpError(
                code = 400,
                message = "Invalid credentials"
            )
        }
        
        val currentTime = System.currentTimeMillis()
        
        val mockUser = UserDto(
            id = "user_${'$'}{loginRequest.username}",
            username = loginRequest.username,
            email = "${'$'}{loginRequest.username}@example.com",
            firstName = "John",
            lastName = "Doe",
            isActive = true,
            createdAt = currentTime - 86400000, // 1 day ago
            lastLoginAt = currentTime
        )
        
        val mockToken = TokenDto(
            accessToken = "mock_access_token_${'$'}{System.currentTimeMillis()}",
            refreshToken = "mock_refresh_token_${'$'}{System.currentTimeMillis()}",
            tokenType = "Bearer",
            expiresIn = 3600, // 1 hour
            issuedAt = currentTime,
            scope = "read write"
        )
        
        // Store for other operations
        currentUser = mockUser
        currentToken = mockToken
        
        return DataResult.Success(
            LoginResponseDto(
                user = mockUser,
                token = mockToken
            )
        )
    }
    
    override suspend fun refreshToken(refreshToken: String): DataResult<TokenDto> {
        delay(500) // Simulate network delay
        
        if (refreshToken.isBlank()) {
            return DataResult.Error.Network.HttpError(
                code = 401,
                message = "Invalid refresh token"
            )
        }
        
        val currentTime = System.currentTimeMillis()
        val newToken = TokenDto(
            accessToken = "mock_refreshed_access_token_${'$'}currentTime",
            refreshToken = "mock_refreshed_refresh_token_${'$'}currentTime",
            tokenType = "Bearer",
            expiresIn = 3600,
            issuedAt = currentTime,
            scope = "read write"
        )
        
        currentToken = newToken
        
        return DataResult.Success(newToken)
    }
    
    override suspend fun logout(): DataResult<Unit> {
        delay(300) // Simulate network delay
        
        currentUser = null
        currentToken = null
        
        return DataResult.Success(Unit)
    }
    
    override suspend fun getCurrentUser(): DataResult<UserDto> {
        delay(200) // Simulate network delay
        
        return currentUser?.let { user ->
            DataResult.Success(user)
        } ?: DataResult.Error.Network.HttpError(
            code = 401,
            message = "User not authenticated"
        )
    }
    
    override suspend fun validateToken(token: String): DataResult<Boolean> {
        delay(100) // Simulate network delay
        
        val isValid = token.isNotBlank() && currentToken?.accessToken == token
        return DataResult.Success(isValid)
    }
}