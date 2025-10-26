package com.gpcasiapac.storesystems.core.identity.api

import kotlinx.coroutines.flow.Flow

/**
 * Lightweight session storage facade for identity-related session values.
 * Backed by DataStore in identity-data.
 */
interface SessionRepository {
    fun userIdFlow(): Flow<String?>
    suspend fun setUserId(userId: String)

    fun accessTokenFlow(): Flow<String?>
    suspend fun setAccessToken(token: String)

    /** Clears all stored session values (user id, token, etc.). */
    suspend fun clear()
}