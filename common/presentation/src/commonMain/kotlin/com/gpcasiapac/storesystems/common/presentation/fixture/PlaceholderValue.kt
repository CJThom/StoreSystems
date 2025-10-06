package com.gpcasiapac.storesystems.common.presentation.fixture

import kotlin.random.Random

/**
 * Generic placeholder value generators for runtime loading states with Modifier.placeholder().
 * 
 * These values are intentionally obvious ("#######") to catch bugs where placeholders 
 * leak through the placeholder modifier system.
 * 
 * Features:
 * - Fixed-length strings for consistent fields (e.g., invoice numbers)
 * - Variable-length strings for dynamic fields (e.g., customer names)
 * - Easy to change the character type (currently "#")
 */
object PlaceholderValue {
    
    /**
     * The character used for placeholder generation.
     * Change this single value to switch all placeholders (e.g., to '█', '_', etc.)
     */
    private const val PLACEHOLDER_CHAR = '#'
    
    /**
     * Standard lengths for common UI patterns
     */
    object Length {
        const val SHORT = 8       // Short labels, codes
        const val MEDIUM = 12     // Names, titles
        const val LONG = 20       // Addresses, descriptions
        const val EXTRA_LONG = 32 // Long text fields
    }
    
    /**
     * Generate a fixed-length placeholder string
     * 
     * @param length The exact length of the placeholder string
     * @return A string of repeated placeholder characters
     */
    fun fixed(length: Int = Length.MEDIUM): String = 
        PLACEHOLDER_CHAR.toString().repeat(length)
    
    /**
     * Generate a variable-length placeholder string with randomized length
     * 
     * This is useful for fields that should have varying lengths in the UI,
     * like customer names, to make the placeholder effect more realistic.
     * 
     * @param minLength Minimum length (inclusive)
     * @param maxLength Maximum length (inclusive)
     * @return A string of random length between min and max
     */
    fun variable(minLength: Int = Length.SHORT, maxLength: Int = Length.LONG): String {
        val length = Random.nextInt(minLength, maxLength + 1)
        return PLACEHOLDER_CHAR.toString().repeat(length)
    }
    
    /**
     * Preset fixed-length placeholder strings for common use cases
     */
    object Presets {
        val short = fixed(Length.SHORT)           // "########"
        val medium = fixed(Length.MEDIUM)         // "############"
        val long = fixed(Length.LONG)             // "####################"
        val extraLong = fixed(Length.EXTRA_LONG)  // 32 hashes
    }
}
