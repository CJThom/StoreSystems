package com.gpcasiapac.storesystems.core.identity.api

import com.gpcasiapac.storesystems.common.kotlin.DataResult
import com.gpcasiapac.storesystems.core.identity.api.model.AuthSession
import com.gpcasiapac.storesystems.core.identity.api.model.User
import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
import kotlinx.coroutines.flow.Flow

/**
 * Public facade for identity operations. Implemented in identity-impl using domain use cases.
 */
interface IdentityService {
    suspend fun login(username: String, password: String): DataResult<AuthSession>
    suspend fun getUser(userId: UserId): User?
    suspend fun isLoggedIn(): Boolean
    suspend fun logout()

    /** Flow of the current logged-in user's id, or null if not logged in. */
    fun observeCurrentUserId(): Flow<UserId?>
}

