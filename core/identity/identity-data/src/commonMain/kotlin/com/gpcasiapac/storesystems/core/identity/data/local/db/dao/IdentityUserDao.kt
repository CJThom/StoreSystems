package com.gpcasiapac.storesystems.core.identity.data.local.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.gpcasiapac.storesystems.core.identity.data.local.db.entity.UserEntity

@Dao
interface IdentityUserDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(user: UserEntity)

    @Query("SELECT * FROM identity_users WHERE username = :username LIMIT 1")
    suspend fun getByUsername(username: String): UserEntity?

    @Query("DELETE FROM identity_users")
    suspend fun clear()

}
