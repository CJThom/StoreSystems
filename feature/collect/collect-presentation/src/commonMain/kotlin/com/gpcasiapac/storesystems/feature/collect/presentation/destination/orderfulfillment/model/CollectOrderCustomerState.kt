package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderfulfillment.model

import androidx.compose.runtime.Immutable
import com.gpcasiapac.storesystems.common.presentation.fixture.PlaceholderValue
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType

@Immutable
data class CollectOrderCustomerState(
    val type: CustomerType,
    val name: String,
    val customerNumber: String,
    val mobileNumber: String?
) {

    companion object {

        fun placeholder(index: Int = 0): CollectOrderCustomerState {
            return CollectOrderCustomerState(
                type = if (index % 2 == 0) CustomerType.B2C else CustomerType.B2B,
                name = PlaceholderValue.variable(minLength = 10, maxLength = 25),
                customerNumber = PlaceholderValue.fixed(8),
                mobileNumber = PlaceholderValue.fixed(10)
            )
        }

        fun placeholderList(count: Int = 6): List<CollectOrderCustomerState> {
            return  List(count) { index -> placeholder(index) }
        }

    }

}

