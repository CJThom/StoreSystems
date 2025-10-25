package com.gpcasiapac.storesystems.core.preferences.data

import com.gpcasiapac.storesystems.common.di.ModuleProvider
import com.gpcasiapac.storesystems.core.preferences.api.PreferencesRepository
import kotlinx.serialization.json.Json
import org.koin.core.module.Module
import org.koin.core.module.dsl.bind
import org.koin.core.module.dsl.singleOf
import org.koin.dsl.module

val preferencesDataModule = module {
    // Expect a DataStore<Preferences> to be provided by the host app per platform.
    // Provide a Json instance if not already bound.
    single<Json> { Json { ignoreUnknownKeys = true; encodeDefaults = true } }
    singleOf(::PreferencesRepositoryDataStore) { bind<PreferencesRepository>() }
}

object PreferencesDataModuleProvider : ModuleProvider {
    override fun modules(): List<Module> = listOf(preferencesDataModule)
}
