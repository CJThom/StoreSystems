package com.gpcasiapac.storesystems.common.persistence.db

import androidx.room.RoomDatabase

/**
 * Generic cross-platform finalizer to build a RoomDatabase with the right driver
 * and coroutine context for each platform.
 */
expect object RoomDbFinalizer {
    fun <T : RoomDatabase> finalize(builder: RoomDatabase.Builder<T>): T
}
