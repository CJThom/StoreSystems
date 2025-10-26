package com.gpcasiapac.storesystems.feature.collect.data.local.db.converter

import androidx.room.TypeConverter
import com.gpcasiapac.storesystems.feature.collect.domain.model.SortOption

class SortOptionConverters {

    @TypeConverter
    fun fromString(value: String?): SortOption? = value?.let { runCatching { SortOption.valueOf(it) }.getOrNull() }

    @TypeConverter
    fun toString(value: SortOption?): String? = value?.name
}
