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
import com.gpcasiapac.storesystems.feature.collect.domain.usecase.workorder.EnsureAndApplyOrderSelectionDeltaUseCase
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
    private val ensureAndApplyOrderSelectionDeltaUseCase: EnsureAndApplyOrderSelectionDeltaUseCase,
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
                    handleSelect()
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

    private suspend fun handleSelect() {
        val session = sessionState.value
        val res = ensureAndApplyOrderSelectionDeltaUseCase(
            userId = session.userId,
            currentSelectedWorkOrderId = session.workOrderId,
            toAdd = listOf(invoiceKey.value),
            toRemove = emptyList()
        )
        when (res) {
            is EnsureAndApplyOrderSelectionDeltaUseCase.Result.Summary -> {
                val inv = invoiceKey.value
                if (inv in res.added) {
                    setEffect {
                        OrderDetailsScreenContract.Effect.Outcome.Selected(invoiceNumber = inv)
                    }
                } else if (inv in res.duplicates) {
                    // Optional: show subtle feedback; currently no-op
                }
            }
            is EnsureAndApplyOrderSelectionDeltaUseCase.Result.Error -> {
                setState { copy(error = res.message) }
                setEffect {
                    OrderDetailsScreenContract.Effect.ShowSnackbar(
                        message = res.message,
                        duration = SnackbarDuration.Long
                    )
                }
            }
            is EnsureAndApplyOrderSelectionDeltaUseCase.Result.Noop -> {
                // No changes applied; ignore
            }
        }
    }

    private suspend fun fetchOrders(successToast: String) {
        setState { copy(isLoading = true, error = null) }

        when (val result = fetchOrderListUseCase()) {
            is FetchOrderListUseCase.UseCaseResult.Success -> {
                setState { copy(isLoading = false) }
            }

            is FetchOrderListUseCase.UseCaseResult.Error -> {
                val msg = result.message
                setState { copy(isLoading = false, error = msg) }
                setEffect {
                    OrderDetailsScreenContract.Effect.ShowSnackbar(
                        msg,
                        duration = SnackbarDuration.Long
                    )
                }
            }
        }

    }

    private fun WorkOrderId?.handleNull(): WorkOrderId? {
        if (this == null) {
            setState { copy(error = "No Work Order Selected") }
        }
        return this
    }

}