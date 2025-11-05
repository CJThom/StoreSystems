package com.gpcasiapac.storesystems.core.identity.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.core.module.Module
import org.koin.dsl.module

actual val identityDataStoreModule: Module = module {
    single<DataStore<Preferences>> { createDataStore { DATA_STORE_FILE_NAME } }
}