package com.gpcasiapac.storesystems.core.identity.data.network.source

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.data.network.dto.LoginRequestDto
import com.gpcasiapac.storesystems.core.identity.data.network.dto.LoginResponseDto
import com.gpcasiapac.storesystems.core.identity.data.network.dto.TokenDto
import com.gpcasiapac.storesystems.core.identity.data.network.dto.UserDto
import kotlinx.coroutines.delay

class MockIdentityNetworkDataSourceImpl : IdentityNetworkDataSource {

    private var currentUser: UserDto? = null
    private var currentToken: TokenDto? = null

    override suspend fun login(loginRequest: LoginRequestDto): DataResult<LoginResponseDto> {
        delay(1000)
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
            createdAt = currentTime - 86400000,
            lastLoginAt = currentTime
        )
        val mockToken = TokenDto(
            accessToken = "mock_access_token_${'$'}{System.currentTimeMillis()}",
            refreshToken = "mock_refresh_token_${'$'}{System.currentTimeMillis()}",
            tokenType = "Bearer",
            expiresIn = 3600,
            issuedAt = currentTime,
            scope = "read write"
        )
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
        delay(500)
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
        delay(300)
        currentUser = null
        currentToken = null
        return DataResult.Success(Unit)
    }

    override suspend fun getCurrentUser(): DataResult<UserDto> {
        delay(200)
        return currentUser?.let { user ->
            DataResult.Success(user)
        } ?: DataResult.Error.Network.HttpError(
            code = 401,
            message = "User not authenticated"
        )
    }

    override suspend fun validateToken(token: String): DataResult<Boolean> {
        delay(100)
        val isValid = token.isNotBlank() && currentToken?.accessToken == token
        return DataResult.Success(isValid)
    }

    // For testing: seed a user and token
    suspend fun seedUser(username: String) {
        val currentTime = System.currentTimeMillis()
        currentUser = UserDto(
            id = "user_${'$'}username",
            username = username,
            email = "${'$'}username@example.com",
            firstName = "John",
            lastName = "Doe",
            isActive = true,
            createdAt = currentTime - 86400000,
            lastLoginAt = currentTime
        )
        currentToken = TokenDto(
            accessToken = "mock_access_token_${'$'}currentTime",
            refreshToken = "mock_refresh_token_${'$'}currentTime",
            tokenType = "Bearer",
            expiresIn = 3600,
            issuedAt = currentTime,
            scope = "read write"
        )
    }
}
