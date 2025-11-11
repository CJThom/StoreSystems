package com.gpcasiapac.storesystems.common.kotlin.util

/**
 * String utility functions for common string operations.
 */
object StringUtils {
    /**
     * Combines first and last name into a full name string.
     * Filters out null or blank values and joins with a space.
     *
     * @param firstName The first name (optional)
     * @param lastName The last name (optional)
     * @return A full name string with non-blank names joined by space, or empty string if both are blank/null
     */
    fun fullName(firstName: String?, lastName: String?): String {
        val cleanedFirstName = firstName?.trim()?.takeIf { it.isNotEmpty() }
        val cleanedLastName = lastName?.trim()?.takeIf { it.isNotEmpty() }
        
        return when {
            cleanedFirstName != null && cleanedLastName != null -> "$cleanedFirstName $cleanedLastName"
            cleanedFirstName != null -> cleanedFirstName
            cleanedLastName != null -> cleanedLastName
            else -> ""
        }
    }
}
