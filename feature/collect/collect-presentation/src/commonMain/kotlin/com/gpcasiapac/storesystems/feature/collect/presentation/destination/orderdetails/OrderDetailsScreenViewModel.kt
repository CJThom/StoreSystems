package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetails

import androidx.compose.material3.SnackbarDuration
import androidx.lifecycle.viewModelScope
import com.gpcasiapac.storesystems.common.feedback.haptic.HapticEffect
import com.gpcasiapac.storesystems.common.feedback.sound.SoundEffect
import com.gpcasiapac.storesystems.common.presentation.mvi.MVIViewModel
import com.gpcasiapac.storesystems.common.presentation.session.SessionHandler
import com.gpcasiapac.storesystems.common.presentation.session.SessionHandlerDelegate
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectSessionIds
import com.gpcasiapac.storesystems.feature.collect.domain.model.value.WorkOrderId
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.order.CheckOrderExistsUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.order.FetchOrderListUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.order.ObserveCollectOrderWithCustomerWithLineItemsUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.prefs.GetCollectSessionIdsFlowUseCase
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.AddOrderListToCollectWorkOrderUseCase
import com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderlist.mapper.toState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch

class OrderDetailsScreenViewModel(
    private val fetchOrderListUseCase: FetchOrderListUseCase,
    private val observeCollectOrderWithCustomerWithLineItemsUseCase: ObserveCollectOrderWithCustomerWithLineItemsUseCase,
    private val addOrderListToCollectWorkOrderUseCase: AddOrderListToCollectWorkOrderUseCase,
    private val checkOrderExistsUseCase: CheckOrderExistsUseCase,
    private val initialInvoice: String,
    private val collectSessionIdsFlowUseCase: GetCollectSessionIdsFlowUseCase
) : MVIViewModel<
        OrderDetailsScreenContract.Event,
        OrderDetailsScreenContract.State,
        OrderDetailsScreenContract.Effect>(),
    SessionHandlerDelegate<CollectSessionIds> by SessionHandler(
        initialSession = CollectSessionIds(),
        sessionFlow = collectSessionIdsFlowUseCase()
    ) {

    private val invoiceKey = MutableStateFlow(initialInvoice.trim())

    override fun setInitialState(): OrderDetailsScreenContract.State {
        return OrderDetailsScreenContract.State(
            order = null,
            isLoading = true,
            error = null
        )
    }

    override fun onStart() {
        // Single reactive pipeline that switches the observed order when invoiceKey changes
        viewModelScope.launch {
            invoiceKey
                .map { it.trim() }
                .filter { it.isNotEmpty() }
                .distinctUntilChanged { a, b -> a.equals(b, ignoreCase = true) }
                .onEach { setState { copy(isLoading = true, error = null) } }
                .flatMapLatest { invoice ->
                    observeCollectOrderWithCustomerWithLineItemsUseCase(invoice)
                        .map { domain -> domain?.toState() }
                        .catch { e ->
                            val msg = e.message ?: "Failed to load order"
                            setState { copy(isLoading = false, error = msg) }
                            setEffect {
                                OrderDetailsScreenContract.Effect.ShowSnackbar(
                                    msg,
                                    duration = SnackbarDuration.Long
                                )
                            }
                            emit(null)
                        }
                }
                .collectLatest { orderState ->
                    if (orderState != null) {
                        setState { copy(order = orderState, isLoading = false, error = null) }
                    } else {
                        setState {
                            copy(
                                order = null,
                                isLoading = false,
                                error = "Order not found"
                            )
                        }
                        setEffect {
                            OrderDetailsScreenContract.Effect.ShowSnackbar(
                                "Order not found",
                                duration = SnackbarDuration.Long
                            )
                        }
                    }
                }
        }
    }

    override fun handleEvents(event: OrderDetailsScreenContract.Event) {
        when (event) {
            is OrderDetailsScreenContract.Event.Refresh -> {
                viewModelScope.launch { fetchOrders(successToast = "Orders refreshed") }
            }

            is OrderDetailsScreenContract.Event.Back -> {
                setEffect { OrderDetailsScreenContract.Effect.Outcome.Back }
            }

            is OrderDetailsScreenContract.Event.Select -> {
                viewModelScope.launch {
                    val workOrderId: WorkOrderId =
                        sessionState.value.workOrderId.handleNull() ?: return@launch

                    val current = invoiceKey.value

                    runCatching {
                        addOrderListToCollectWorkOrderUseCase(
                            workOrderId = workOrderId,
                            orderIdList = listOf(current)
                        )
                    }.onSuccess {
                        setEffect {
                            OrderDetailsScreenContract.Effect.Outcome.Selected(
                                current
                            )
                        }
                    }.onFailure { t ->
                        val msg = t.message ?: "Failed to select order. Please try again."
                        setState { copy(error = msg) }
                        setEffect {
                            OrderDetailsScreenContract.Effect.ShowSnackbar(
                                msg,
                                duration = SnackbarDuration.Long
                            )
                        }
                    }
                }
            }

            is OrderDetailsScreenContract.Event.ScanInvoice -> {
                val invoice = event.invoiceNumber.trim()
                viewModelScope.launch {
                    when (val result = checkOrderExistsUseCase(invoice)) {
                        is CheckOrderExistsUseCase.UseCaseResult.Exists -> {
                            val target = result.invoiceNumber
                            if (!target.equals(invoiceKey.value, ignoreCase = true)) {
                                invoiceKey.value = target
                            }
                        }

                        is CheckOrderExistsUseCase.UseCaseResult.Error -> {
                            setEffect { OrderDetailsScreenContract.Effect.PlayHaptic(HapticEffect.Error) }
                            setEffect { OrderDetailsScreenContract.Effect.PlaySound(SoundEffect.Error) }
                            setEffect { OrderDetailsScreenContract.Effect.ShowSnackbar(result.message) }
                        }
                    }
                }
            }
        }
    }

    private suspend fun fetchOrders(successToast: String) {
        setState { copy(isLoading = true, error = null) }
        val result = fetchOrderListUseCase()
        result.fold(
            onSuccess = {
                setState { copy(isLoading = false) }
                setEffect { OrderDetailsScreenContract.Effect.ShowSnackbar(successToast) }
            },
            onFailure = { t ->
                val msg = t.message ?: "Failed to refresh orders. Please try again."
                setState { copy(isLoading = false, error = msg) }
                setEffect {
                    OrderDetailsScreenContract.Effect.ShowSnackbar(
                        msg,
                        duration = SnackbarDuration.Long
                    )
                }
            }
        )
    }

    private fun WorkOrderId?.handleNull(): WorkOrderId? {
        if (this == null) {
            setState { copy(error = "No Work Order Selected") }
        }
        return this
    }


}