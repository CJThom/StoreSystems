package com.gpcasiapac.storesystems.feature.collect.presentation.destination.orderdetail

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.gpcasiapac.storesystems.feature.collect.presentation.util.displayName
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter

@Composable
fun OrderDetailScreen(
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
                // Order list or single title
                if (state.orderList.isNotEmpty()) {
                    Text("Order list", style = MaterialTheme.typography.titleLarge)
                    Spacer(Modifier.height(8.dp))
                    state.orderList.forEach { order ->
                        Text("• ${'$'}{order.displayName}", style = MaterialTheme.typography.bodyLarge)
                    }
                } else {
                    val title = state.order?.displayName ?: "Order ${'$'}{state.orderId}"
                    Text(title, style = MaterialTheme.typography.titleLarge)
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
                if (state.isSigned) {
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
private fun OrderDetailScreenPreview(
    @PreviewParameter(OrderDetailScreenStateProvider::class) state: OrderDetailScreenContract.State
) {
    GPCTheme {
        OrderDetailScreen(
            state = state,
            onEventSent = {},
            effectFlow = null,
            onOutcome = {}
        )
    }
}
