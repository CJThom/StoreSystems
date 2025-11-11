package com.gpcasiapac.storesystems.feature.collect.domain.usecase.order

import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderLocalRepository
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRemoteRepository

/**
 * Fetches orders from the remote repository and persists them into the local database
 * atomically. Network fetch is done outside the DB transaction; upsert happens inside.
 */
class FetchOrderListUseCase(
    private val orderRemoteRepository: OrderRemoteRepository,
    private val orderLocalRepository: OrderLocalRepository,
) {

    suspend operator fun invoke(clearBeforeInsert: Boolean = false): UseCaseResult {
        return try {
            // 1) Fetch from backend as domain models
            val orders = orderRemoteRepository.fetchOrders()

            // 2) Persist atomically
            orderLocalRepository.write {
                if (clearBeforeInsert) {
                    // Optional future hook; for now, upsertOrders can replace existing rows.
                    // If a full replace is desired, expose a clearOrders() on local and call it here.
                }
                orderLocalRepository.upsertOrders(orders)
            }
            UseCaseResult.Success(fetchedCount = orders.size)
        } catch (t: Throwable) {
            UseCaseResult.Error.Unexpected(
                t.message ?: "Failed to refresh orders. Please try again."
            )
        }
    }

    sealed interface UseCaseResult {
        data class Success(val fetchedCount: Int) : UseCaseResult
        sealed class Error(open val message: String) : UseCaseResult {
            data class Unexpected(override val message: String) : Error(message)
        }
    }

}
