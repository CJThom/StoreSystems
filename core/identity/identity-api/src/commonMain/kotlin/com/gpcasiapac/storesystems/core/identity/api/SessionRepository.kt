package com.gpcasiapac.storesystems.core.identity.api

import com.gpcasiapac.storesystems.core.identity.api.model.value.UserId
import kotlinx.coroutines.flow.Flow

/**
 * Lightweight session storage facade for identity-related session values.
 * Backed by DataStore in identity-data.
 */
interface SessionRepository {
    fun userIdFlow(): Flow<UserId?>
    suspend fun setUserId(userId: UserId)

    fun accessTokenFlow(): Flow<String?>
    suspend fun setAccessToken(token: String)

    /** Clears all stored session values (user id, token, etc.). */
    suspend fun clear()
}