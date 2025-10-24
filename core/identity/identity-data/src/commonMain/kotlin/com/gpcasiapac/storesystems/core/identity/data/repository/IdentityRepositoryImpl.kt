package com.gpcasiapac.storesystems.core.identity.data.repository

import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.api.model.AuthSession
import com.gpcasiapac.storesystems.core.identity.api.model.Token
import com.gpcasiapac.storesystems.core.identity.api.model.User
import com.gpcasiapac.storesystems.core.identity.data.local.db.IdentityDatabase
import com.gpcasiapac.storesystems.core.identity.data.local.db.dao.AuthSessionDao
import com.gpcasiapac.storesystems.core.identity.data.local.db.dao.IdentityUserDao
import com.gpcasiapac.storesystems.core.identity.data.local.db.entity.SessionEntity
import com.gpcasiapac.storesystems.core.identity.data.mapper.toDomain
import com.gpcasiapac.storesystems.core.identity.data.mapper.toEntity
import com.gpcasiapac.storesystems.core.identity.data.mapper.toDomain as entityToDomain
import com.gpcasiapac.storesystems.core.identity.data.mapper.toAuthSessionEntity
import com.gpcasiapac.storesystems.core.identity.data.network.dto.LoginRequestDto
import com.gpcasiapac.storesystems.core.identity.data.network.source.IdentityNetworkDataSource
import com.gpcasiapac.storesystems.core.identity.domain.repository.IdentityRepository
import co.touchlab.kermit.Logger

class IdentityRepositoryImpl(
    private val networkDataSource: IdentityNetworkDataSource,
    private val userDao: IdentityUserDao,
    private val sessionDao: AuthSessionDao,
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
                // Persist user and session atomically
                database.useWriterConnection { transactor ->
                    transactor.immediateTransaction {
                        userDao.upsert(user.toEntity())
                        sessionDao.upsert(toAuthSessionEntity(user, token))
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
            val token = r.data.toDomain()
            // Update token in existing session if present
            try {
                database.useWriterConnection { transactor ->
                    transactor.immediateTransaction {
                        val current = sessionDao.getSession()
                        if (current != null) {
                            sessionDao.upsert(
                                SessionEntity(
                                    singleton = 1,
                                    userId = current.userId,
                                    accessToken = token.accessToken,
                                    refreshToken = token.refreshToken,
                                    tokenType = token.tokenType,
                                    expiresIn = token.expiresIn,
                                    issuedAt = token.issuedAt,
                                    scope = token.scope,
                                )
                            )
                        }
                    }
                }
            } catch (db: Throwable) {
                log.e(db) { "Failed to persist refreshed token" }
            }
            DataResult.Success(token)
        } catch (e: Exception) {
            DataResult.Error.Client.Mapping("Failed to map token refresh response", e)
        }
        is DataResult.Error -> r
    }

    override suspend fun logout(): DataResult<Unit> = when (val r = networkDataSource.logout()) {
        is DataResult.Success -> {
            // Clear session locally
            try {
                database.useWriterConnection { transactor ->
                    transactor.immediateTransaction { sessionDao.clear() }
                }
            } catch (db: Throwable) {
                log.e(db) { "Failed to clear session on logout" }
            }
            DataResult.Success(Unit)
        }
        is DataResult.Error -> r
    }

    override suspend fun getCurrentUser(): DataResult<User> {
        // Prefer local cached user via session
        return try {
            val session = sessionDao.getSession()
            if (session != null) {
                val user = userDao.getByUsername(session.userId)
                if (user != null) return DataResult.Success(user.entityToDomain())
            }
            // Fallback to network
            when (val r = networkDataSource.getCurrentUser()) {
                is DataResult.Success -> try {
                    val user = r.data.toDomain()
                    try {
                        database.useWriterConnection { transactor ->
                            transactor.immediateTransaction { userDao.upsert(user.toEntity()) }
                        }
                    } catch (db: Throwable) {
                        log.e(db) { "Failed to cache current user" }
                    }
                    DataResult.Success(user)
                } catch (e: Exception) {
                    DataResult.Error.Client.Mapping("Failed to map current user response", e)
                }
                is DataResult.Error -> r
            }
        } catch (e: Exception) {
            // If DB access failed unexpectedly, fallback to network best-effort
            when (val r = networkDataSource.getCurrentUser()) {
                is DataResult.Success -> try {
                    DataResult.Success(r.data.toDomain())
                } catch (e2: Exception) {
                    DataResult.Error.Client.Mapping("Failed to map current user response", e2)
                }
                is DataResult.Error -> r
            }
        }
    }

    override suspend fun validateToken(token: String): DataResult<Boolean> = when (val r = networkDataSource.validateToken(token)) {
        is DataResult.Success -> DataResult.Success(r.data)
        is DataResult.Error -> r
    }
}
