package com.gpcasiapac.storesystems.core.identity.api

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.api.model.AuthSession
import com.gpcasiapac.storesystems.core.identity.api.model.Token
import com.gpcasiapac.storesystems.core.identity.api.model.User

/**
 * Public facade for identity operations. Implemented in identity-impl using domain use cases.
 */
interface IdentityService {
    suspend fun login(username: String, password: String): DataResult<AuthSession>
    suspend fun getUser(userId: String): User?
    suspend fun isLoggedIn(): Boolean
    suspend fun logout()
}

