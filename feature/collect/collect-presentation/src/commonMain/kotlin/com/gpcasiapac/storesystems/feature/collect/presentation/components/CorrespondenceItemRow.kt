package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.minimumInteractiveComponentSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.tooling.preview.Preview
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme

@Composable
fun CorrespondenceItemRow(
    type: String,
    detail: String,
    onCheckChange: () -> Unit,
    modifier: Modifier = Modifier,
    onEdit: (() -> Unit)? = null,
    isEnabled: Boolean = true,
    contentPadding: PaddingValues = PaddingValues(Dimens.Space.small)
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clickable(
                onClick = onCheckChange,
                role = Role.Checkbox
            )
            .padding(contentPadding),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
    ) {

        Checkbox(
            checked = isEnabled,
            onCheckedChange = null,
            modifier = Modifier.minimumInteractiveComponentSize()
        )

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = type,
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = detail,
                style = MaterialTheme.typography.bodySmall,
            )
        }

        if (onEdit != null) {
            IconButton(onClick = onEdit) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Edit",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(Dimens.Size.iconSmall)
                )
            }
        }
    }
}

@Preview
@Composable
fun CorrespondenceItemRowPreview() {
    GPCTheme {
        Surface {
            CorrespondenceItemRow(
                type = "Email",
                detail = "Send email to customer",
                isEnabled = false,
                onCheckChange = {},
                onEdit = {}
            )
        }
    }
}