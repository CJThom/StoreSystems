package com.gpcasiapac.storesystems.feature.login.data.repository

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.feature.login.data.network.source.LoginNetworkDataSource
import com.gpcasiapac.storesystems.feature.login.data.network.dto.LoginRequestDto
import com.gpcasiapac.storesystems.feature.login.data.mapper.toDomain
import com.gpcasiapac.storesystems.feature.login.domain.repository.LoginRepository
import com.gpcasiapac.storesystems.feature.login.domain.repository.LoginResult

class LoginRepositoryImpl(
    private val networkDataSource: LoginNetworkDataSource
) : LoginRepository {
    
    override suspend fun login(username: String, password: String): DataResult<LoginResult> {
        return try {
            val loginRequest = LoginRequestDto(username = username, password = password)
            when (val result = networkDataSource.login(loginRequest)) {
                is DataResult.Success -> {
                    try {
                        DataResult.Success(result.data.toDomain())
                    } catch (e: Exception) {
                        DataResult.Error.Client.Mapping(
                            message = "Failed to map login response",
                            throwable = e
                        )
                    }
                }
                is DataResult.Error -> result // Pass through the error from network layer
            }
        } catch (e: Exception) {
            DataResult.Error.Client.UnexpectedError(
                message = "Failed to create login request",
                throwable = e
            )
        }
    }
}