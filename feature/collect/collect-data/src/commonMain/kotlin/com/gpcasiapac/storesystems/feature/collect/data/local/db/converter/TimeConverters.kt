package com.gpcasiapac.storesystems.feature.collect.data.local.db.converter

import androidx.room.TypeConverter
import kotlin.time.Instant

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
