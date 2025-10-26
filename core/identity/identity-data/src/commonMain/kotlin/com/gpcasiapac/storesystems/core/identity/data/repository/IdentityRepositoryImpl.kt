package com.gpcasiapac.storesystems.core.identity.data.repository

import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.api.model.AuthSession
import com.gpcasiapac.storesystems.core.identity.api.model.Token
import com.gpcasiapac.storesystems.core.identity.api.model.User
import com.gpcasiapac.storesystems.core.identity.data.local.db.IdentityDatabase
import com.gpcasiapac.storesystems.core.identity.data.local.db.dao.IdentityUserDao
import com.gpcasiapac.storesystems.core.identity.data.mapper.toDomain
import com.gpcasiapac.storesystems.core.identity.data.mapper.toEntity
import com.gpcasiapac.storesystems.core.identity.data.network.dto.LoginRequestDto
import com.gpcasiapac.storesystems.core.identity.data.network.source.IdentityNetworkDataSource
import com.gpcasiapac.storesystems.core.identity.domain.repository.IdentityRepository
import co.touchlab.kermit.Logger

class IdentityRepositoryImpl(
    private val networkDataSource: IdentityNetworkDataSource,
    private val userDao: IdentityUserDao,
    private val database: IdentityDatabase,
    private val logger: Logger,
) : IdentityRepository {

    private val log = logger.withTag("IdentityRepository")

    override suspend fun login(username: String, password: String): DataResult<AuthSession> = try {
        val request = LoginRequestDto(username = username, password = password)
        when (val r = networkDataSource.login(request)) {
            is DataResult.Success -> try {
                val user = r.data.user.toDomain()
                val token = r.data.token.toDomain()
                // Persist user only; session is owned by core:session now
                database.useWriterConnection { transactor ->
                    transactor.immediateTransaction {
                        userDao.upsert(user.toEntity())
                    }
                }
                DataResult.Success(AuthSession(user = user, token = token))
            } catch (e: Exception) {
                DataResult.Error.Client.Mapping("Failed to map or persist login response", e)
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

    override suspend fun getCurrentUser(): DataResult<User> =
        when (val r = networkDataSource.getCurrentUser()) {
            is DataResult.Success -> try {
                val user = r.data.toDomain()
                // Cache user for faster cold starts
                runCatching {
                    database.useWriterConnection { transactor ->
                        transactor.immediateTransaction { userDao.upsert(user.toEntity()) }
                    }
                }.onFailure { db -> log.e(db) { "Failed to cache current user" } }
                DataResult.Success(user)
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
