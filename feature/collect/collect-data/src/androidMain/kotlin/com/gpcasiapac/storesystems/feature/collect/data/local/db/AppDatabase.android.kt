package com.gpcasiapac.storesystems.feature.collect.data.local.db

import androidx.room.RoomDatabase
import com.gpcasiapac.storesystems.common.persistence.db.RoomDbFinalizer

actual fun getRoomDatabase(
    builder: RoomDatabase.Builder<AppDatabase>
): AppDatabase {
    return RoomDbFinalizer.finalize(builder)
}
