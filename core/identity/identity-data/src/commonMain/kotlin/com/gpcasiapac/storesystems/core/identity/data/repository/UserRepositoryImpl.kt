package com.gpcasiapac.storesystems.core.identity.data.repository

import androidx.room.immediateTransaction
import androidx.room.useWriterConnection
import co.touchlab.kermit.Logger
import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.api.model.AuthSession
import com.gpcasiapac.storesystems.core.identity.api.model.User
import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
import com.gpcasiapac.storesystems.core.identity.data.local.db.IdentityDatabase
import com.gpcasiapac.storesystems.core.identity.data.local.db.dao.IdentityUserDao
import com.gpcasiapac.storesystems.core.identity.data.mapper.toDomain
import com.gpcasiapac.storesystems.core.identity.data.mapper.toEntity
import com.gpcasiapac.storesystems.core.identity.data.network.dto.LoginRequestDto
import com.gpcasiapac.storesystems.core.identity.data.network.source.IdentityNetworkDataSource
import com.gpcasiapac.storesystems.core.identity.domain.repository.UserRepository

class UserRepositoryImpl(
    private val networkDataSource: IdentityNetworkDataSource,
    private val userDao: IdentityUserDao,
    private val database: IdentityDatabase,
    private val logger: Logger
) : UserRepository {

    private val log = logger.withTag("IdentityRepository")

    override suspend fun login(username: String, password: String): DataResult<AuthSession> = try {
        val request = LoginRequestDto(username = username, password = password)
        when (val r = networkDataSource.login(request)) {
            is DataResult.Success -> try {
                val user = r.data.user.toDomain()
                val token = r.data.token.toDomain()
                // Persist user entity
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


    override suspend fun getUser(userId: UserId): User? {
        return userDao.getByUsername(userId)?.toDomain()
    }
}
