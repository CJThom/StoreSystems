package com.gpcasiapac.storesystems.core.identity.data.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.PreferenceDataStoreFactory
import okio.Path.Companion.toPath
import org.koin.core.module.Module

// Expect/Actual Koin module that provides a platform DataStore<Preferences>
expect val identityDataStoreModule: Module

// Shared helper and stable filename following the coreDataStoreModule pattern
internal const val DATA_STORE_FILE_NAME: String = "prefs.preferences_pb"

fun createDataStore(producePath: () -> String): DataStore<Preferences> {
    return PreferenceDataStoreFactory.createWithPath(
        produceFile = { producePath().toPath() }
    )
}
