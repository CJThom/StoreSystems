package com.gpcasiapac.storesystems.feature.collect.presentation.mapper

import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.foundation.component.CustomerTypeParam

/**
 * Maps domain CustomerType to foundation CustomerTypeParam for UI components
 */
fun CustomerType.toParam(): CustomerTypeParam {
    return when (this) {
        CustomerType.B2B -> CustomerTypeParam.B2B
        CustomerType.B2C -> CustomerTypeParam.B2C
    }
}
