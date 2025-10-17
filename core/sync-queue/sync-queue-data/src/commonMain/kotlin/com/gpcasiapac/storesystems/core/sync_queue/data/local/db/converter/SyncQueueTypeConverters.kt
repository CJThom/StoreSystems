package com.gpcasiapac.storesystems.core.sync_queue.data.local.db.converter

import androidx.room.TypeConverter
import com.gpcasiapac.storesystems.core.sync_queue.domain.model.SyncTaskAttemptError
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class SyncQueueTypeConverters {
    
    private val json = Json { 
        ignoreUnknownKeys = true
        encodeDefaults = true
    }
    
    @TypeConverter
    fun fromErrorAttemptsList(errorAttempts: List<SyncTaskAttemptError>?): String? {
        return errorAttempts?.let { json.encodeToString(it) }
    }
    
    @TypeConverter
    fun toErrorAttemptsList(errorAttemptsJson: String?): List<SyncTaskAttemptError>? {
        return errorAttemptsJson?.let { 
            try {
                json.decodeFromString<List<SyncTaskAttemptError>>(it)
            } catch (e: Exception) {
                emptyList()
            }
        }
    }
}