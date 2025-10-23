package com.gpcasiapac.storesystems.common.scanning

import kotlinx.coroutines.flow.Flow

/**
 * A very small registry to expose a Flow of ScanResult to UI layers without
 * binding them to platform-specific scanner implementations.
 *
 * App code on Android should set [provider] to return the DataWedge scanner flow.
 * Other platforms can leave it null.
 */
object ScanEventsRegistry {
    @Volatile
    var provider: (() -> Flow<ScanResult>?)? = null
}
