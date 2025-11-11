package com.gpcasiapac.storesystems.core.identity.data.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import org.koin.core.module.Module
import org.koin.dsl.module

actual val identityDataStoreModule: Module = module {
    // Provide a single, application-scoped Preferences DataStore for identity session values
    single<DataStore<Preferences>> { createDataStore(context = get()) }
}

fun createDataStore(context: Context): DataStore<Preferences> {
    return createDataStore {
        context.filesDir.resolve(DATA_STORE_FILE_NAME).absolutePath
    }
}
