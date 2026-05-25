package com.corridometro.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.corridometro.domain.DayHistoryItem
import com.corridometro.ui.theme.AppColors
import com.corridometro.util.formatCurrency
import com.corridometro.util.formatDate

@Composable
fun DayHistoryCard(
    item: DayHistoryItem,
    onOpenReport: () -> Unit,
    modifier: Modifier = Modifier,
    showPlatforms: Boolean = false,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .clickable(onClick = onOpenReport),
        shape = MaterialTheme.shapes.medium,
        color = AppColors.surfaceVariant(),
        border = BorderStroke(1.dp, AppColors.outline()),
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    formatDate(item.dateEpochDay),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleSmall,
                    color = AppColors.onSurface(),
                )
                Text(
                    "${item.shiftCount} jornada(s) · ${item.tripCount} corridas",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.onSurfaceVariant(),
                )
                Text(
                    "Faturamento bruto ${formatCurrency(item.grossEarnings)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.onSurfaceVariant(),
                )
                if (showPlatforms && item.platformLabels.isNotEmpty()) {
                    Text(
                        "Apps: ${item.platformLabels.joinToString(", ")}",
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.onSurfaceVariant(),
                    )
                }
                Text(
                    if (item.isFinalized) "Relatório finalizado" else "Toque para ver relatório",
                    style = MaterialTheme.typography.labelMedium,
                    color = if (item.isFinalized) AppColors.primary() else AppColors.onSurfaceVariant(),
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
            OutlinedButton(onClick = onOpenReport) {
                Text("Ver relatório", color = AppColors.primary())
            }
        }
    }
}
