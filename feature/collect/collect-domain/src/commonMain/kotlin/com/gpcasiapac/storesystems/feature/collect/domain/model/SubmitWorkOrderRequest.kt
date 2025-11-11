package com.gpcasiapac.storesystems.feature.collect.domain.model

data class SubmitWorkOrderRequest(
    val id: String,
    val orderChannel: String,
    val customerSignature: CustomerSignatureRequest,
    val courierName: String,
    val submitTimestamp: String,
    val repId: String,
    val invoices: List<String>
)

data class CustomerSignatureRequest(
    val signature: String,
    val name: String,
    val signatureAt: String
)
