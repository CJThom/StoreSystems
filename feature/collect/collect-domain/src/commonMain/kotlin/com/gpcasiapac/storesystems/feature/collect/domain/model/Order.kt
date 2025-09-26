package com.gpcasiapac.storesystems.feature.collect.domain.model

import kotlin.time.Instant

 data class Order(
     val id: String,
     val customerType: CustomerType,
     val customerName: String,
     val invoiceNumber: String,
     val webOrderNumber: String?,
     val pickedAt: Instant,
 )