package com.gpcasiapac.storesystems.feature.collect.data.local.db.dao

import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType

// --- Scoped suggestion variants (apply invoice scope in SQL) ---
data class CustomerNameRowWithType(
    val name: String,
    val customerType: CustomerType
)