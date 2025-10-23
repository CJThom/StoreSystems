package com.gpcasiapac.storesystems.core.sync_queue.data.local.db.converter

import androidx.room.TypeConverter
import com.gpcasiapac.storesystems.core.sync_queue.api.model.SyncTaskAttemptError
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class SyncQueueTypeConverters {
    
    @TypeConverter
    fun fromErrorAttemptsList(errorAttempts: List<SyncTaskAttemptError>?): String? {
        if (errorAttempts == null) return null
        
        // Manually build JSON array to avoid kotlinx-serialization KSP issues
        val jsonItems = errorAttempts.joinToString(",") { error ->
            """{"attemptNumber":${error.attemptNumber},"timestamp":${error.timestamp.toEpochMilliseconds()},"errorMessage":"${error.errorMessage.replace("\"", "\\\"")}"}"""
        }
        return "[$jsonItems]"
    }

    @TypeConverter
    fun toErrorAttemptsList(errorAttemptsJson: String?): List<SyncTaskAttemptError>? {
        if (errorAttemptsJson.isNullOrEmpty() || errorAttemptsJson == "[]") return emptyList()
        
        return try {
            // Manual JSON parsing to avoid kotlinx-serialization KSP issues
            val items = mutableListOf<SyncTaskAttemptError>()
            val jsonArray = errorAttemptsJson.trim().removeSurrounding("[", "]")
            
            if (jsonArray.isEmpty()) return emptyList()
            
            // Simple regex-based parsing for the specific structure
            val itemRegex = """"attemptNumber":(\d+),"timestamp":(\d+),"errorMessage":"((?:[^"\\]|\\.)*)"""".toRegex()
            itemRegex.findAll(jsonArray).forEach { match ->
                val attemptNumber = match.groupValues[1].toInt()
                val timestamp = Instant.fromEpochMilliseconds(match.groupValues[2].toLong())
                val errorMessage = match.groupValues[3].replace("\\\"", "\"")
                items.add(SyncTaskAttemptError(attemptNumber, timestamp, errorMessage))
            }
            items
        } catch (e: Exception) {
            emptyList()
        }
    }
}