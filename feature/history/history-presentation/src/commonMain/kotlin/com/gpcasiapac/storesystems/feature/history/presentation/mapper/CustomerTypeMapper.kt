package com.gpcasiapac.storesystems.feature.history.presentation.mapper

import com.gpcasiapac.storesystems.foundation.component.CustomerTypeParam

/**
 * Maps String customerType from history domain to foundation CustomerTypeParam
 */
fun String.toCustomerTypeParam(): CustomerTypeParam {
    return when (this.uppercase()) {
        "B2B" -> CustomerTypeParam.B2B
        "B2C" -> CustomerTypeParam.B2C
        else -> CustomerTypeParam.B2C // Default fallback
    }
}
