package com.gpcasiapac.storesystems.feature.collect.api

import androidx.navigation3.runtime.NavKey
import kotlinx.serialization.Serializable

//@Serializable
interface CollectFeatureDestination : NavKey {
  //  @Serializable
    data object Orders : CollectFeatureDestination

  //  @Serializable
    data class OrderDetails(val orderId: String) : CollectFeatureDestination
}
