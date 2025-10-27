package com.gpcasiapac.storesystems.feature.collect.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

@Serializable
sealed interface CollectFeatureDestination : NavKey {
    @Serializable
    data object Orders : CollectFeatureDestination

    @Serializable
    data class OrderDetails(val invoiceNumber: String): CollectFeatureDestination

    @Serializable
    data object OrderFulfilment : CollectFeatureDestination

    @Serializable
    data class WorkOrderDetails(val invoiceNumber: String): CollectFeatureDestination

    @Serializable
    data class Signature(val customerName: String) : CollectFeatureDestination
}
