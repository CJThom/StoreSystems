package com.gpcasiapac.storesystems.core.preferences.data

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.gpcasiapac.storesystems.core.preferences.api.AppId
import com.gpcasiapac.storesystems.core.preferences.api.FeatureId
import com.gpcasiapac.storesystems.core.preferences.api.PreferenceKeyDef
import com.gpcasiapac.storesystems.core.preferences.api.PreferenceScope
import com.gpcasiapac.storesystems.core.preferences.api.PreferencesRepository
import com.gpcasiapac.storesystems.core.preferences.api.Principal
import com.gpcasiapac.storesystems.core.preferences.api.ScreenId
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.map
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json

class PreferencesRepositoryDataStore(
    private val dataStore: DataStore<Preferences>,
    private val json: Json,
) : PreferencesRepository {

    override fun <T> flow(
        key: PreferenceKeyDef<T>,
        principal: Principal?,
        scope: PreferenceScope,
        enableFallbacks: Boolean
    ): Flow<T> {
        val chain = if (enableFallbacks) buildChain(principal, scope) else listOf(Path.of(principal, scope))
        return dataStore.data.map { prefs ->
            var found: T? = null
            for (p in chain) {
                val raw = prefs[pathKey(p, key)]
                if (raw != null) {
                    found = json.decodeFromString(key.serializer, raw)
                    break
                }
            }
            found ?: key.default
        }
    }

    override suspend fun <T> set(
        key: PreferenceKeyDef<T>,
        principal: Principal?,
        scope: PreferenceScope,
        value: T
    ) {
        val p = Path.of(principal, scope)
        val jsonString = json.encodeToString(key.serializer, value)
        dataStore.edit { prefs ->
            prefs[pathKey(p, key)] = jsonString
        }
    }

    override suspend fun clear(principal: Principal?, scope: PreferenceScope) {
        // Optional: Implement a side index if scope clearing is needed often
    }

    private data class Path(val user: String?, val app: AppId?, val feature: String?, val screen: String?) {
        companion object {
            fun of(principal: Principal?, scope: PreferenceScope): Path {
                val user = (principal as? Principal.User)?.username
                return when (scope) {
                    PreferenceScope.Global -> Path(user, null, null, null)
                    is PreferenceScope.App -> Path(user, scope.appId, null, null)
                    is PreferenceScope.Feature -> Path(user, scope.appId, scope.feature.value, null)
                    is PreferenceScope.Screen -> Path(user, scope.appId, scope.feature.value, scope.screen.value)
                }
            }
        }
    }

    private fun pathKey(path: Path, key: PreferenceKeyDef<*>): androidx.datastore.preferences.core.Preferences.Key<String> {
        val user = path.user ?: "device"
        val app = path.app?.name ?: "_"
        val feature = path.feature ?: "_"
        val screen = path.screen ?: "_"
        val stable = key.id
        val k = "p:$user|$app|$feature|$screen|$stable"
        return stringPreferencesKey(k)
    }

    private fun buildChain(principal: Principal?, scope: PreferenceScope): List<Path> {
        val s = Path.of(principal, scope)
        val device = Path.of(Principal.Device, scope)
        return buildList {
            add(s)
            add(s.copy(screen = null))
            add(s.copy(feature = null, screen = null))
            add(s.copy(app = null, feature = null, screen = null))
            add(device)
            add(device.copy(screen = null))
            add(device.copy(feature = null, screen = null))
            add(device.copy(app = null, feature = null, screen = null))
        }
    }
}
