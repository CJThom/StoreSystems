package com.gpcasiapac.storesystems.feature.collect.domain.model

/**
 * Customer details grouped in a single model.
 * Kept minimal based on requirements and current UI usage.
 */
data class Customer(
    val customerNumber: String,
    val customerType: CustomerType,
    val accountName: String?,
    val firstName: String?,
    val lastName: String?,
    val phone: String?
) {

    // TODO: Move out of this class
    val fullName: String
        get() = listOfNotNull(
            firstName?.takeIf { it.isNotBlank() },
            lastName?.takeIf { it.isNotBlank() })
            .joinToString(" ")
}
