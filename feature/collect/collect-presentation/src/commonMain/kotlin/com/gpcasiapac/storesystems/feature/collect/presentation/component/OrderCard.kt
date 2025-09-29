package com.gpcasiapac.storesystems.feature.collect.presentation.component

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.gpcasiapac.storesystems.feature.collect.domain.model.Order
import com.gpcasiapac.storesystems.feature.collect.presentation.util.displayName
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.jetbrains.compose.ui.tooling.preview.PreviewParameter
import kotlin.time.Clock
import kotlin.time.Duration.Companion.hours
import kotlin.time.Duration.Companion.minutes

/**
 * Reusable order card used by multiple screens (Order list and Order detail multi-order section).
 * Keeps visuals minimal and dependency-free so it can be dropped anywhere.
 */
@Composable
fun OrderCard(
    order: Order,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val clickableCard: @Composable (content: @Composable () -> Unit) -> Unit = { content ->
        if (onClick != null) {
            androidx.compose.material3.Card(
                onClick = onClick,
                modifier = modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) { content() }
        } else {
            androidx.compose.material3.Card(
                modifier = modifier.fillMaxWidth(),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) { content() }
        }
    }

    clickableCard {
        Column(modifier = Modifier.fillMaxWidth().padding(12.dp)) {
            // Title line: display name (account name for B2B, person for B2C)
            Text(
                text = order.displayName,
                style = MaterialTheme.typography.titleMedium,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(Modifier.size(6.dp))
            // Invoice and Web order inline with time since picked
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(order.invoiceNumber, style = MaterialTheme.typography.bodySmall)
                if (!order.webOrderNumber.isNullOrBlank()) {
                    Text(
                        "  •  ",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Text(order.webOrderNumber!!, style = MaterialTheme.typography.bodySmall)
                }
                Spacer(Modifier.weight(1f))
                AssistChip(
                    onClick = {},
                    label = { Text(formatPickedRelative(order)) },
                )
            }
        }
    }
}

private fun formatPickedRelative(order: Order): String {
    val now = Clock.System.now()
    val minutes = (now - order.pickedAt).inWholeMinutes
    return when {
        minutes < 1 -> "Just now"
        minutes < 60 -> "${minutes} minutes"
        minutes < 60 * 24 -> "${minutes / 60} hours"
        else -> "${minutes / (60 * 24)} days"
    }
}

// ---- Previews ----

private val now get() = Clock.System.now()

private fun previewOrders(): List<Order> = listOf(
    Order(
        id = "1",
        customerType = com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType.B2C,
        accountName = null,
        invoiceNumber = "INV-1001",
        webOrderNumber = "WEB-7771",
        pickedAt = now - 12.minutes,
        customer = com.gpcasiapac.storesystems.feature.collect.domain.model.Customer(
            customerNumber = "CUST-0001",
            customerType = com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType.B2C,
            accountName = null,
            firstName = "Jane",
            lastName = "Doe",
            phone = "+65 8123 4567",
        )
    ),
    Order(
        id = "2",
        customerType = com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType.B2B,
        accountName = "Acme Corp",
        invoiceNumber = "INV-1002",
        webOrderNumber = null,
        pickedAt = now - 2.hours,
        customer = com.gpcasiapac.storesystems.feature.collect.domain.model.Customer(
            customerNumber = "ACC-0002",
            customerType = com.gpcasiapac.storesystems.feature.collect.domain.model.CustomerType.B2B,
            accountName = "Acme Corp",
            firstName = null,
            lastName = null,
            phone = "+65 8000 0002",
        )
    )
)

class OrderCardStateProvider : org.jetbrains.compose.ui.tooling.preview.PreviewParameterProvider<Order> {
    override val values: Sequence<Order>
        get() {
            val orders = previewOrders()
            return sequenceOf(orders[0], orders[1])
        }
}

@Preview(name = "Order card", showBackground = true)
@Composable
private fun OrderCardPreview(
    @PreviewParameter(OrderCardStateProvider::class) order: Order
) {
    com.gpcasiapac.storesystems.foundation.design_system.GPCTheme {
        Card(onClick = {}, modifier = Modifier.fillMaxWidth()) {
            Column(Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OrderCard(order = order, onClick = {})
            }
        }
    }
}
