package com.gpcasiapac.storesystems.core.identity.data.local.db

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.RoomDatabaseConstructor
import com.gpcasiapac.storesystems.core.identity.data.local.db.dao.AuthSessionDao
import com.gpcasiapac.storesystems.core.identity.data.local.db.dao.IdentityUserDao
import com.gpcasiapac.storesystems.core.identity.data.local.db.entity.SessionEntity
import com.gpcasiapac.storesystems.core.identity.data.local.db.entity.UserEntity
import kotlinx.coroutines.Dispatchers

@Database(
    entities = [
        UserEntity::class,
        SessionEntity::class,
    ],
    version = 1,
    exportSchema = true,
)
@ConstructedBy(IdentityDatabaseConstructor::class)
abstract class IdentityDatabase : RoomDatabase() {
    abstract fun userDao(): IdentityUserDao
    abstract fun sessionDao(): AuthSessionDao
}

@Suppress("KotlinNoActualForExpect")
expect object IdentityDatabaseConstructor : RoomDatabaseConstructor<IdentityDatabase> {
    override fun initialize(): IdentityDatabase
}

fun getRoomDatabase(
    builder: RoomDatabase.Builder<IdentityDatabase>
): IdentityDatabase {
    return builder
        .fallbackToDestructiveMigration(true)
        .setQueryCoroutineContext(Dispatchers.IO)
        .setJournalMode(RoomDatabase.JournalMode.WRITE_AHEAD_LOGGING)
        .build()
}
