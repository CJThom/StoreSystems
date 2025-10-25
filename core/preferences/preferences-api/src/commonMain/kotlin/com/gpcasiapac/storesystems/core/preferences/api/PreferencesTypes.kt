package com.gpcasiapac.storesystems.core.preferences.api

import kotlinx.serialization.KSerializer

// Who the preference applies to
sealed interface Principal {
    data object Device : Principal
    data class User(val username: String) : Principal
}

// Where in the app hierarchy the preference applies
sealed class PreferenceScope {
    data object Global : PreferenceScope()
    data class App(val appId: AppId) : PreferenceScope()
    data class Feature(val appId: AppId, val feature: FeatureId) : PreferenceScope()
    data class Screen(val appId: AppId, val feature: FeatureId, val screen: ScreenId) : PreferenceScope()
}

// Type-safe identifiers to avoid stringly-typed mistakes
enum class AppId { Super, Collect, Messenger, Picking }

interface FeatureId { val value: String }
interface ScreenId { val value: String }

// Strongly-typed key definition
interface PreferenceKeyDef<T> {
    val id: String // stable storage id
    val serializer: KSerializer<T>
    val default: T
}

// Repository API
interface PreferencesRepository {
    fun <T> flow(
        key: PreferenceKeyDef<T>,
        principal: Principal?,
        scope: PreferenceScope,
        enableFallbacks: Boolean = true,
    ): kotlinx.coroutines.flow.Flow<T>

    suspend fun <T> set(
        key: PreferenceKeyDef<T>,
        principal: Principal?,
        scope: PreferenceScope,
        value: T
    )

    suspend fun clear(principal: Principal?, scope: PreferenceScope)
}
