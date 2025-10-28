package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomer
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import kotlinx.coroutines.flow.first

/**
 * Use case to get a single CollectOrderWithCustomer by invoice number.
 * Used when we need to fetch full order data from just an invoice number.
 */
class GetOrderByInvoiceNumberUseCase(
    private val orderRepository: OrderRepository
) {
    suspend operator fun invoke(invoiceNumber: String): Result<CollectOrderWithCustomer> {
        return try {
            val orderWithLineItems = orderRepository
                .getCollectOrderWithCustomerWithLineItemsFlow(invoiceNumber)
                .first()
            
            if (orderWithLineItems == null) {
                Result.failure(IllegalArgumentException("Order with invoice number $invoiceNumber not found"))
            } else {
                // Convert CollectOrderWithCustomerWithLineItems to CollectOrderWithCustomer
                val orderWithCustomer = CollectOrderWithCustomer(
                    order = orderWithLineItems.order,
                    customer = orderWithLineItems.customer
                )
                Result.success(orderWithCustomer)
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
