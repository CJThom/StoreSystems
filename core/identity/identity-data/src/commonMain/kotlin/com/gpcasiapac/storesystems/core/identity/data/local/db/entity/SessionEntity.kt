package com.gpcasiapac.storesystems.core.identity.data.local.db.entity

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "auth_session")
data class SessionEntity(

    @PrimaryKey
    @ColumnInfo(name = "singleton")
    val singleton: Int = 1,

    @ColumnInfo(name = "user_id")
    val userId: String,

    @ColumnInfo(name = "access_token")
    val accessToken: String,

    @ColumnInfo(name = "refresh_token")
    val refreshToken: String,

    @ColumnInfo(name = "token_type")
    val tokenType: String,

    @ColumnInfo(name = "expires_in")
    val expiresIn: Long,

    @ColumnInfo(name = "issued_at")
    val issuedAt: Long,

    @ColumnInfo(name = "scope")
    val scope: String?,

)
