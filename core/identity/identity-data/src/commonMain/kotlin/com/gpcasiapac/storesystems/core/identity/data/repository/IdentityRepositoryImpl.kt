package com.gpcasiapac.storesystems.core.identity.data.repository

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.api.model.AuthSession
import com.gpcasiapac.storesystems.core.identity.api.model.Token
import com.gpcasiapac.storesystems.core.identity.api.model.User
import com.gpcasiapac.storesystems.core.identity.data.mapper.toDomain
import com.gpcasiapac.storesystems.core.identity.data.network.dto.LoginRequestDto
import com.gpcasiapac.storesystems.core.identity.data.network.source.IdentityNetworkDataSource
import com.gpcasiapac.storesystems.core.identity.domain.repository.IdentityRepository

class IdentityRepositoryImpl(
    private val networkDataSource: IdentityNetworkDataSource
) : IdentityRepository {

    override suspend fun login(username: String, password: String): DataResult<AuthSession> = try {
        val request = LoginRequestDto(username = username, password = password)
        when (val r = networkDataSource.login(request)) {
            is DataResult.Success -> try {
                val user = r.data.user.toDomain()
                val token = r.data.token.toDomain()
                DataResult.Success(AuthSession(user = user, token = token))
            } catch (e: Exception) {
                DataResult.Error.Client.Mapping("Failed to map login response", e)
            }
            is DataResult.Error -> r
        }
    } catch (e: Exception) {
        DataResult.Error.Client.UnexpectedError("Failed to create login request", e)
    }

    override suspend fun refreshToken(refreshToken: String): DataResult<Token> = when (val r = networkDataSource.refreshToken(refreshToken)) {
        is DataResult.Success -> try {
            DataResult.Success(r.data.toDomain())
        } catch (e: Exception) {
            DataResult.Error.Client.Mapping("Failed to map token refresh response", e)
        }
        is DataResult.Error -> r
    }

    override suspend fun logout(): DataResult<Unit> = when (val r = networkDataSource.logout()) {
        is DataResult.Success -> DataResult.Success(Unit)
        is DataResult.Error -> r
    }

    override suspend fun getCurrentUser(): DataResult<User> = when (val r = networkDataSource.getCurrentUser()) {
        is DataResult.Success -> try {
            DataResult.Success(r.data.toDomain())
        } catch (e: Exception) {
            DataResult.Error.Client.Mapping("Failed to map current user response", e)
        }
        is DataResult.Error -> r
    }

    override suspend fun validateToken(token: String): DataResult<Boolean> = when (val r = networkDataSource.validateToken(token)) {
        is DataResult.Success -> DataResult.Success(r.data)
        is DataResult.Error -> r
    }
}
