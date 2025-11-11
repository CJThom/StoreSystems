package com.gpcasiapac.storesystems.core.sync_queue.data.local.db.converter

import androidx.room.TypeConverter
import kotlin.time.ExperimentalTime
import kotlin.time.Instant

@OptIn(ExperimentalTime::class)
class TimeConverters {

    @TypeConverter
    fun fromEpochMillis(value: Long?): Instant? {
        return value?.let { Instant.fromEpochMilliseconds(it) }
    }

    @TypeConverter
    fun toEpochMillis(value: Instant?): Long? {
        return value?.toEpochMilliseconds()
    }
}
