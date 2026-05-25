package com.corridometro.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.corridometro.domain.Platform
import com.corridometro.domain.PlatformMoreApps
import com.corridometro.ui.theme.TextSecondary

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PlatformMoreSheet(
    onDismiss: () -> Unit,
    onSelect: (Platform) -> Unit,
    selected: Platform? = null,
    selectedPlatforms: Set<Platform> = emptySet(),
    multiSelect: Boolean = false,
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp),
        ) {
            Text(
                text = "Outros aplicativos",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = if (multiSelect) {
                    "Toque para incluir ou tirar do filtro. Feche quando terminar."
                } else {
                    "Escolha o app em que você rodou ou ganhou no dia."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 16.dp),
            )
            FlowRow(
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp),
            ) {
                PlatformMoreApps.forEach { platform ->
                    val isSelected = if (multiSelect) platform in selectedPlatforms else selected == platform
                    PlatformLogoChip(
                        platform = platform,
                        selected = isSelected,
                        onClick = {
                            onSelect(platform)
                            if (!multiSelect) onDismiss()
                        },
                    )
                }
            }
        }
    }
}
