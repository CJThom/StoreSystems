package com.gpcasiapac.storesystems.feature.collect.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.calculateEndPadding
import androidx.compose.foundation.layout.calculateStartPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalLayoutDirection
import com.gpcasiapac.storesystems.foundation.design_system.Dimens
import com.gpcasiapac.storesystems.foundation.design_system.GPCTheme
import org.jetbrains.compose.ui.tooling.preview.Preview

@Composable
fun CorrespondenceSection(
    correspondenceOptionList: List<CorrespondenceItemDisplayParam>,
    onCheckedChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(horizontal = Dimens.Space.medium),
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(Dimens.Space.medium)
    ) {

        Text(
            text = "Correspondence",
            style = MaterialTheme.typography.titleMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(
                top = contentPadding.calculateTopPadding(),
                start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
                end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
            )
        )

        Column {
            correspondenceOptionList.forEach { correspondenceOption ->
                CorrespondenceItemRow(
                    type = correspondenceOption.type,
                    detail = correspondenceOption.detail,
                    isEnabled = correspondenceOption.isEnabled,
                    onCheckChange = {
                        onCheckedChange(correspondenceOption.id)
                    },
                    contentPadding = PaddingValues(
                        top = Dimens.Space.small,
                        start = contentPadding.calculateStartPadding(LocalLayoutDirection.current),
                        end = contentPadding.calculateEndPadding(LocalLayoutDirection.current),
                        bottom = Dimens.Space.small,
                    )
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun CorrespondenceSectionPreview() {
    GPCTheme {
        CorrespondenceSection(
            correspondenceOptionList = listOf(
                CorrespondenceItemDisplayParam(
                    id = "email",
                    type = "Email",
                    detail = "Send email to customer",
                    isEnabled = true
                ),
                CorrespondenceItemDisplayParam(
                    id = "print",
                    type = "Print",
                    detail = "Send invoice to printer",
                    isEnabled = false
                )
            ),
            onCheckedChange = { _ -> }
        )
    }
}