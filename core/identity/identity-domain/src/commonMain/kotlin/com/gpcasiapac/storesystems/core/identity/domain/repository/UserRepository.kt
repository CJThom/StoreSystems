package com.gpcasiapac.storesystems.core.identity.domain.repository

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.api.model.AuthSession
import com.gpcasiapac.storesystems.core.identity.api.model.User

interface UserRepository {

    suspend fun login(username: String, password: String): DataResult<AuthSession>

    suspend fun getUser(userId: String): User?

}
