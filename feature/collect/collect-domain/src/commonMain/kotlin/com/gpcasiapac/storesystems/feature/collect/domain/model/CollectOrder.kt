package com.gpcasiapac.storesystems.feature.collect.domain.model

import com.gpcasiapac.storesystems.feature.collect.api.model.InvoiceNumber
import kotlin.time.Instant

data class CollectOrder(
    val id: String,
    val invoiceNumber: InvoiceNumber,
    val orderNumber: String,
    val webOrderNumber: String?,
    val orderChannel: OrderChannel,
    val invoiceDateTime: Instant,
    val createdDateTime: Instant,
    val isLocked: Boolean,
    val lockedBy: String?,
    val lockedDateTime: Instant?
)