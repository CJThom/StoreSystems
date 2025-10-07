package com.gpcasiapac.storesystems.feature.collect.domain.util

import com.gpcasiapac.storesystems.common.kotlin.util.StringUtils
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType


/**
 * A utility object for formatting customer-related data into display strings.
 */
object CustomerNameFormatter {

    /**
     * Generates a display name for a customer based on their type and name details.
     *
     * @param customerType The type of the customer (B2B or B2C).
     * @param accountName The account name, used for B2B customers.
     * @param firstName The first name, used for B2C customers.
     * @param lastName The last name, used for B2C customers.
     * @return A formatted customer name, or "-" if a valid name cannot be constructed.
     */
    fun getDisplayName(
        customerType: CustomerType,
        accountName: String?,
        firstName: String?,
        lastName: String?
    ): String {
        return when (customerType) {
            CustomerType.B2B -> accountName.takeIf { !it.isNullOrBlank() } ?: "-"
            CustomerType.B2C -> StringUtils.fullName(
                firstName = firstName,
                lastName = lastName
            ).takeIf { it.isNotEmpty() } ?: "-"
        }
    }

}