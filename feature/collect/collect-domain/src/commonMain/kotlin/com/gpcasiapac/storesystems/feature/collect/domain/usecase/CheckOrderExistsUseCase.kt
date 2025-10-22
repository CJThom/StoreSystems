package com.gpcasiapac.storesystems.feature.collect.domain.usecase

import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRepository
import com.gpcasiapac.storesystems.feature.collect.domain.repository.SearchQuery
import kotlinx.coroutines.flow.first

/**
 * Minimal existence check for an invoice number using existing search flow.
 * We avoid the single-row relation Flow because it throws if no rows exist.
 */
class CheckOrderExistsUseCase(
    private val orderRepository: OrderRepository,
) {
    suspend operator fun invoke(invoiceNumber: String): Boolean {
        val list = orderRepository.observeSearchOrders(SearchQuery(invoiceNumber)).first()
        return list.any { it.order.invoiceNumber.equals(invoiceNumber, ignoreCase = true) }
    }
}
