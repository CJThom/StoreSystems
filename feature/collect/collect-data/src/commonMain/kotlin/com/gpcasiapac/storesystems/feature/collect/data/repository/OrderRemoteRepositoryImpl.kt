package com.gpcasiapac.storesystems.feature.collect.data.repository
import com.gpcasiapac.storesystems.feature.collect.data.mapper.toDomain
import com.gpcasiapac.storesystems.feature.collect.data.network.dto.CustomerSignatureDto
import com.gpcasiapac.storesystems.feature.collect.data.network.dto.OrderChannelDto
import com.gpcasiapac.storesystems.feature.collect.data.network.dto.SubmitCollectOrderRequestDto
import com.gpcasiapac.storesystems.feature.collect.data.network.source.OrderNetworkDataSource
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectOrderWithCustomerWithLineItems
import com.gpcasiapac.storesystems.feature.collect.domain.model.SubmitWorkOrderRequest
import com.gpcasiapac.storesystems.feature.collect.domain.repository.OrderRemoteRepository

/** Network-only implementation that fetches orders and maps them to domain models. */
class OrderRemoteRepositoryImpl(
    private val network: OrderNetworkDataSource,
) : OrderRemoteRepository {
    override suspend fun fetchOrders(): List<CollectOrderWithCustomerWithLineItems> {
        val dtos = network.fetchOrders()
        // Map CollectOrderDto directly to domain models (no relation hop)
        return dtos.toDomain()
    }

    override suspend fun submitWorkOrder(request: SubmitWorkOrderRequest): Result<Unit> {
        val dto = SubmitCollectOrderRequestDto(
            id = request.id,
            orderChannel = when (request.orderChannel.uppercase()) {
                "B2B" -> OrderChannelDto.B2B
                else -> OrderChannelDto.B2C
            },
            customerSignature = CustomerSignatureDto(
                signature = request.customerSignature.signature,
                name = request.customerSignature.name,
                signatureAt = request.customerSignature.signatureAt
            ),
            courierName = request.courierName,
            submitTimestamp = request.submitTimestamp,
            repId = request.repId,
            invoices = request.invoices
        )
        return network.submitOrder(dto)
    }
}
