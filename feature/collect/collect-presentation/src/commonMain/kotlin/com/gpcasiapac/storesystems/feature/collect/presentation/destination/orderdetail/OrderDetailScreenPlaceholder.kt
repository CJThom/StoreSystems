package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.collect.domain.model.CollectingType
import com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType
import com.gpcasiapac.storesystems.feature.collect.domain.model.Order
import com.gpcasiapac.storesystems.feature.collect.presentation.component.OrderCard
import com.gpcasiapac.storesystems.feature.collect.presentation.util.displayName
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import kotlin.time.Clock
import kotlin.time.Instant

@Composable
fun OrderDetailScreenPlaceholder(
    state: OrderDetailScreenContract.State,
    onEventSent: (event: OrderDetailScreenContract.Event) -> Unit,
    effectFlow: Flow<OrderDetailScreenContract.Effect>?,
    onOutcome: (outcome: OrderDetailScreenContract.Effect.Outcome) -> Unit,
) {
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(effectFlow) {
        effectFlow?.collectLatest { effect ->
            when (effect) {
                is OrderDetailScreenContract.Effect.ShowToast ->
                    snackbarHostState.showSnackbar(effect.message, duration = SnackbarDuration.Short)
                is OrderDetailScreenContract.Effect.ShowError ->
                    snackbarHostState.showSnackbar(effect.error, duration = SnackbarDuration.Long)
                is OrderDetailScreenContract.Effect.Outcome -> onOutcome(effect)
            }
        }
    }

    Scaffold(snackbarHost = { SnackbarHost(snackbarHostState) }) { padding ->
        Content(padding, state, onEventSent)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun Content(
    padding: PaddingValues,
    state: OrderDetailScreenContract.State,
    onEventSent: (event: OrderDetailScreenContract.Event) -> Unit,
) {
    Column(
        modifier = Modifier
            .padding(padding)
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        when {
            state.isLoading -> {
                Column(modifier = Modifier.fillMaxWidth(), horizontalAlignment = Alignment.CenterHorizontally) {
                    CircularProgressIndicator()
                    Text("Loading…", modifier = Modifier.padding(top = 8.dp))
                }
            }

            state.error != null -> {
                Text(
                    text = state.error,
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.error,
                )
                Button(onClick = { onEventSent(OrderDetailScreenContract.Event.Refresh) }, modifier = Modifier.padding(top = 12.dp)) {
                    Text("Retry")
                }
            }

            else -> {
                // Order list or single title and details
                if (state.orderList.isNotEmpty()) {
                    Text("Order list", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    state.orderList.forEach { order ->
                        OrderCard(order = order, onClick = {})
                        Spacer(Modifier.height(8.dp))
                    }
                } else {
                    val order = state.order
                    val title = order?.displayName ?: "Order ${state.orderId}"
                    Text(title, style = MaterialTheme.typography.titleLarge)
                    if (order != null) {
                        Spacer(Modifier.height(12.dp))
                        InvoiceHeader(invoiceNumber = order.invoiceNumber)
                        Spacer(Modifier.height(12.dp))
                        OrderMetaGrid(order)
                        Spacer(Modifier.height(16.dp))
                        CustomerBlock(order)
                        Spacer(Modifier.height(16.dp))
                        ProductListPlaceholder(onViewMore = { /* TODO: navigate to items */ })
                    }
                }

                Spacer(Modifier.height(24.dp))
                Text("Who’s collecting?", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    val selected = state.collectingType
                    val types = CollectingType.values()
                    types.forEach { type ->
                        if (type == selected) {
                            Button(onClick = { /* already selected */ }) { Text(type.name.lowercase().replaceFirstChar { it.uppercase() }) }
                        } else {
                            OutlinedButton(onClick = { onEventSent(OrderDetailScreenContract.Event.CollectingChanged(type)) }) {
                                Text(type.name.lowercase().replaceFirstChar { it.uppercase() })
                            }
                        }
                    }
                }

                when (state.collectingType) {
                    CollectingType.ACCOUNT -> {
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = state.representativeSearchText,
                            onValueChange = { onEventSent(OrderDetailScreenContract.Event.RepresentativeSearchChanged(it)) },
                            label = { Text("Search representatives") }
                        )
                        Spacer(Modifier.height(8.dp))
                        Text("Recent representatives", style = MaterialTheme.typography.titleSmall)
                        state.recentRepresentativeList.forEach { rep ->
                            val checked = rep.id in state.selectedRepresentativeIdList
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Checkbox(checked = checked, onCheckedChange = { onEventSent(OrderDetailScreenContract.Event.RepresentativeChecked(rep.id, it)) })
                                Text(rep.name)
                            }
                        }
                    }

                    CollectingType.COURIER -> {
                        Spacer(Modifier.height(16.dp))
                        OutlinedTextField(
                            modifier = Modifier.fillMaxWidth(),
                            value = state.courierName,
                            onValueChange = { onEventSent(OrderDetailScreenContract.Event.CourierNameChanged(it)) },
                            label = { Text("Courier name") },
                            singleLine = true
                        )
                    }

                    CollectingType.STANDARD -> {
                        // No extra input required
                    }
                }

                Spacer(Modifier.height(24.dp))
                Text("Signature", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                if (state.signatureStrokes.isNotEmpty()) {
                    Text("Signature captured ✓", color = MaterialTheme.colorScheme.primary)
                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedButton(onClick = { onEventSent(OrderDetailScreenContract.Event.ClearSignature) }) { Text("Clear") }
                    }
                } else {
                    Button(onClick = { onEventSent(OrderDetailScreenContract.Event.Sign) }) { Text("Sign") }
                }

                Spacer(Modifier.height(24.dp))
                Text("Correspondence", style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(8.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = state.emailChecked, onCheckedChange = { onEventSent(OrderDetailScreenContract.Event.ToggleEmail(it)) })
                    Text("Email")
                    Spacer(Modifier.weight(1f))
                    OutlinedButton(onClick = { onEventSent(OrderDetailScreenContract.Event.EditEmail) }) { Text("Edit") }
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(checked = state.printChecked, onCheckedChange = { onEventSent(OrderDetailScreenContract.Event.TogglePrint(it)) })
                    Text("Print")
                    Spacer(Modifier.weight(1f))
                    OutlinedButton(onClick = { onEventSent(OrderDetailScreenContract.Event.EditPrinter) }) { Text("Edit") }
                }

                Spacer(Modifier.height(24.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedButton(onClick = { onEventSent(OrderDetailScreenContract.Event.Back) }) { Text("Back") }
                    Button(onClick = { onEventSent(OrderDetailScreenContract.Event.Confirm) }) { Text("Confirm") }
                }
            }
        }
    }
}


@Preview(name = "Order detail", showBackground = true, backgroundColor = 0xFFF5F5F5L, widthDp = 360, heightDp = 720)
@Composable
private fun OrderDetailScreenPlaceholderPreview(
    @PreviewParameter(OrderDetailScreenStateProvider::class) state: OrderDetailScreenContract.State
) {
    GPCTheme {
        OrderDetailScreenPlaceholder(
            state = state,
            onEventSent = {},
            effectFlow = null,
            onOutcome = {}
        )
    }
}


@Composable
private fun InvoiceHeader(invoiceNumber: String) {
    Text(
        text = "Invoice: #$invoiceNumber",
        style = MaterialTheme.typography.titleLarge,
        color = MaterialTheme.colorScheme.onSurface,
    )
}

@Composable
private fun SectionTitle(text: String) {
    Text(text, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurface)
}

@Composable
private fun LabeledRow(label: String, value: String?) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(label, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value?.takeIf { it.isNotBlank() } ?: "–", style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun OrderMetaGrid(order: Order) {
    Column(modifier = Modifier.fillMaxWidth()) {
        LabeledRow(label = "Sales Order Number", value = order.invoiceNumber)
        LabeledRow(label = "Web Order Number", value = order.webOrderNumber)
        LabeledRow(label = "Picked", value = formatPickedRelative(order.pickedAt))
    }
}

@Composable
private fun CustomerBlock(order: Order) {
    SectionTitle("Customer details")
    Spacer(Modifier.height(8.dp))
    val isB2B = order.customerType == CustomerType.B2B
    if (isB2B) {
        LabeledRow(label = "Account", value = order.accountName)
    } else {
        val name = order.customer.fullName.ifBlank { null }
        LabeledRow(label = "Name", value = name)
    }
    LabeledRow(label = "Customer Number", value = order.customer.customerNumber)
    LabeledRow(label = "Phone", value = order.customer.phone)
}

@Composable
private fun ProductListPlaceholder(onViewMore: () -> Unit) {
    SectionTitle("Product list")
    Spacer(Modifier.height(8.dp))
    PlaceholderItemRow(title = "Dupli-Color Vinyl & Fabric Paint Gloss Black", sku = "A9442910", qty = 2)
    PlaceholderItemRow(title = "Montana Gold Spray Paint Black", sku = "B4567890", qty = 5)
    Spacer(Modifier.height(8.dp))
    OutlinedButton(onClick = onViewMore) {
        Text("VIEW MORE")
    }
}

@Composable
private fun PlaceholderItemRow(title: String, sku: String, qty: Int) {
    Row(modifier = Modifier.fillMaxWidth().padding(vertical = 6.dp), verticalAlignment = Alignment.CenterVertically) {
        Column(modifier = Modifier.weight(1f)) {
            Text(title, style = MaterialTheme.typography.bodyMedium)
            Text(sku, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.width(12.dp))
        Text("x$qty", style = MaterialTheme.typography.bodyMedium)
    }
}

private fun formatPickedRelative(pickedAt: Instant): String {
    val now = Clock.System.now()
    val duration = now - pickedAt
    val minutes = duration.inWholeMinutes
    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "${minutes}m ago"
        minutes < 60 * 24 -> "${minutes / 60}h ago"
        else -> "${minutes / (60 * 24)}d ago"
    }
}
