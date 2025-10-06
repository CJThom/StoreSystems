package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model

import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import kotlin.time.Instant

@Immutable
data class CollectOrderState(
    val id: String,
    val invoiceNumber: String,
    val webOrderNumber: String?,
    val customerType: CustomerType,
    val customerName: String,
    val pickedAt: Instant,
)