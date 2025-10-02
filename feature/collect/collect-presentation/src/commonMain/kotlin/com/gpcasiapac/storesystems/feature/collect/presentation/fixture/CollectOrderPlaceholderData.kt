package com.gpcasiapac.storesystems.feature.collect.presentation.fixture

import com.gpcasiapac.storesystems.common.presentation.fixture.PlaceholderValue
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.model.CollectOrderState
import kotlin.time.Clock

/**
 * Placeholder data for CollectOrderState using the generic PlaceholderValue generator.
 * 
 * This provides obviously fake data (e.g., "############") that should be hidden by
 * Modifier.placeholder(). If these values leak through due to a bug, they will be
 * immediately apparent in the UI.
 * 
 * Field behavior:
 * - invoiceNumber: Fixed length (consistent across all placeholders)
 * - webOrderNumber: Fixed length (consistent across all placeholders)
 * - customerName: Variable length (randomized for more realistic placeholder effect)
 */
object CollectOrderPlaceholderData {
    
    /**
     * Creates a single placeholder order with obviously fake values
     * 
     * @param index The index of the placeholder (affects customer type alternation)
     * @return A CollectOrderState with placeholder values
     */
    fun single(index: Int = 0): CollectOrderState = CollectOrderState(
        id = "PLACEHOLDER_$index",
        invoiceNumber = PlaceholderValue.fixed(12),  // Fixed length: always 12 chars
        webOrderNumber = PlaceholderValue.fixed(12), // Fixed length: always 12 chars
        customerType = if (index % 2 == 0) CustomerType.B2C else CustomerType.B2B,
        customerName = PlaceholderValue.variable(minLength = 10, maxLength = 25), // Variable length: 10-25 chars
        pickedAt = Clock.System.now()
    )
    
    /**
     * Creates a list of placeholder orders
     * 
     * @param count The number of placeholder orders to create
     * @return A list of CollectOrderState with placeholder values
     */
    fun list(count: Int = 6): List<CollectOrderState> = 
        List(count) { index -> single(index) }
}
