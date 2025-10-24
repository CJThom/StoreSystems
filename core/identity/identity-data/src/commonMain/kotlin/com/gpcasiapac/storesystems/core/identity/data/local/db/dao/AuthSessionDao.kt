package com.gpcasiapac.storesystems.core.identity.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gpcasiapac.storesystems.core.identity.data.local.db.entity.SessionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface AuthSessionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(session: SessionEntity)

    @Query("SELECT * FROM auth_session LIMIT 1")
    fun observeSession(): Flow<SessionEntity?>

    @Query("SELECT * FROM auth_session LIMIT 1")
    suspend fun getSession(): SessionEntity?

    @Query("DELETE FROM auth_session")
    suspend fun clear()

}
