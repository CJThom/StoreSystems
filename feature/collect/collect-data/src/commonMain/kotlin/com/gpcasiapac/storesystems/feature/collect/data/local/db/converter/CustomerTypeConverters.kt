package com.gpcasiapac.storesystems.feature.collect.data.local.db.converter

import androidx.room.TypeConverter
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType

class CustomerTypeConverters {

    @TypeConverter
    fun fromString(value: String?): CustomerType? {
        return value?.let { v ->
            when (v.trim().uppercase()) {
                "B2B" -> CustomerType.B2B
                "B2C" -> CustomerType.B2C
                else -> null
            }
        }
    }

    @TypeConverter
    fun toString(value: CustomerType?): String? {
        return when (value) {
            null -> null
            CustomerType.B2B -> "B2B"
            CustomerType.B2C -> "B2C"
        }
    }
}
