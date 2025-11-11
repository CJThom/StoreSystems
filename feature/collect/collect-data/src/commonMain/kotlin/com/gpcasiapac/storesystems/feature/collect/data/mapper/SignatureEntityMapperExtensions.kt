package com.gpcasiapac.storesystems.feature.collect.data.mapper

import com.gpcasiapac.storesystems.feature.collect.data.local.db.entity.SignatureEntity
import com.gpcasiapac.storesystems.feature.collect.domain.model.Signature

internal fun SignatureEntity.toDomain(): Signature {
    return Signature(
        workOrderId = this.workOrderId,
        signatureBase64 = this.signatureBase64,
        signedByName = this.signedByName,
        signedAt = this.signedAt,
    )
}