package com.gpcasiapac.storesystems.core.identity.data.repository

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.gpcasiapac.storesystems.core.identity.api.SessionRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

/**
 * DataStore-backed implementation of SessionManager.
 * Stores basic identity session values (user id, access token).
 */
class SessionRepositoryImpl(
    private val dataStore: DataStore<Preferences>
) : SessionRepository {

    private companion object {
        val USER_ID_KEY = stringPreferencesKey("identity.user_id")
        val ACCESS_TOKEN_KEY = stringPreferencesKey("identity.access_token")
    }

    override fun userIdFlow(): Flow<String?> =
        dataStore.data.map { it[USER_ID_KEY] }

    override suspend fun setUserId(userId: String) {
        dataStore.edit { prefs ->
            prefs[USER_ID_KEY] = userId
        }
    }

    override fun accessTokenFlow(): Flow<String?> =
        dataStore.data.map { it[ACCESS_TOKEN_KEY] }

    override suspend fun setAccessToken(token: String) {
        dataStore.edit { prefs ->
            prefs[ACCESS_TOKEN_KEY] = token
        }
    }

    override suspend fun clear() {
        dataStore.edit { prefs ->
            prefs.remove(USER_ID_KEY)
            prefs.remove(ACCESS_TOKEN_KEY)
        }
    }
}