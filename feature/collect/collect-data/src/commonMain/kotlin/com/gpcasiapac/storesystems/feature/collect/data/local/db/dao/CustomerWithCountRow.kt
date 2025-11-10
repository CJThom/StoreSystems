package com.gpcasiapac.storesystems.feature.collect.data.local.db.dao

import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType

data class CustomerWithCountRow(
    val name: String,
    val customerType: CustomerType,
    val c: Int
)