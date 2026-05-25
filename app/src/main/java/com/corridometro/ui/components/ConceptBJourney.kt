package com.corridometro.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.corridometro.domain.DayHistoryItem
import com.corridometro.domain.ExpenseCategory
import com.corridometro.domain.Platform
import com.corridometro.domain.WorkShift
import com.corridometro.domain.avgEarningPerTrip
import com.corridometro.domain.fuelCostForShift
import com.corridometro.domain.fuelLitersUsed
import com.corridometro.domain.netProfitForShift
import com.corridometro.domain.profitPerHourForShift
import com.corridometro.domain.profitPerKmForShift
import com.corridometro.domain.shiftDurationMinutes
import com.corridometro.ui.theme.Border
import com.corridometro.ui.theme.Danger
import com.corridometro.ui.theme.DangerSoft
import com.corridometro.ui.theme.Primary
import com.corridometro.ui.theme.PrimarySoft
import com.corridometro.ui.theme.TextSecondary
import com.corridometro.util.formatCurrency
import com.corridometro.util.formatDate
import com.corridometro.util.formatDuration
import com.corridometro.util.formatKm
import com.corridometro.util.formatLiters

/** Conceito B — bloco de preview ao vivo com lucro em destaque. */
@Composable
fun JourneyLivePreviewBlock(
    shift: WorkShift,
    modifier: Modifier = Modifier,
) {
    val profit = netProfitForShift(shift)
    val fuelCost = fuelCostForShift(shift)
    val minutes = shiftDurationMinutes(shift.startMinutesOfDay, shift.endMinutesOfDay)
    val positive = profit >= 0
    val containerColor = if (positive) PrimarySoft else DangerSoft
    val valueColor = if (positive) Primary else Danger

    DashboardContentBlock(
        title = "Lucro estimado",
        subtitle = "${shift.platform.label} · ${formatDate(shift.dateEpochDay)}",
        modifier = modifier,
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = MaterialTheme.shapes.large,
            color = containerColor,
            border = BorderStroke(1.dp, if (positive) Primary.copy(alpha = 0.25f) else Danger.copy(alpha = 0.25f)),
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = formatCurrency(profit),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = valueColor,
                )
                Text(
                    text = "antes de outros gastos do formulário",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(bottom = 12.dp),
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    StatCard(
                        label = "Combustível",
                        value = formatCurrency(fuelCost),
                        modifier = Modifier.weight(1f),
                        valueColor = Danger,
                    )
                    StatCard(
                        label = "R$/km",
                        value = formatCurrency(profitPerKmForShift(shift)),
                        modifier = Modifier.weight(1f),
                    )
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    StatCard(
                        label = "R$/hora",
                        value = formatCurrency(profitPerHourForShift(shift)),
                        modifier = Modifier.weight(1f),
                    )
                    StatCard(
                        label = "Km",
                        value = formatKm(shift.km),
                        modifier = Modifier.weight(1f),
                    )
                }
                Text(
                    text = "${formatLiters(fuelLitersUsed(shift.km, shift.fuelKmPerLiter))} · " +
                        "${shift.tripCount} corridas · ${formatDuration(minutes)} · " +
                        "média/corrida ${formatCurrency(avgEarningPerTrip(shift))}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 10.dp),
                )
            }
        }
    }
}

@Composable
fun JourneyTodayHistoryBlock(
    todayHistory: List<DayHistoryItem>,
    hasAnyHistory: Boolean,
    reportsCount: Int = 0,
    onBrowseAllReports: () -> Unit,
    historyRow: @Composable (DayHistoryItem) -> Unit,
    modifier: Modifier = Modifier,
) {
    DashboardContentBlock(
        title = "Hoje",
        subtitle = "Jornadas registradas no dia atual",
        modifier = modifier,
    ) {
        SecondaryButton(
            text = when {
                !hasAnyHistory -> "Consultar relatórios"
                reportsCount > 0 -> "Consultar todos os relatórios ($reportsCount)"
                else -> "Consultar todos os relatórios"
            },
            onClick = onBrowseAllReports,
            enabled = hasAnyHistory,
            modifier = Modifier.padding(bottom = 10.dp),
        )
        if (todayHistory.isEmpty()) {
            Text(
                text = if (hasAnyHistory) {
                    "Nenhuma jornada registrada hoje."
                } else {
                    "Nenhuma jornada salva ainda."
                },
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
        } else {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                todayHistory.forEach { item ->
                    historyRow(item)
                }
            }
        }
    }
}

@Composable
fun JourneyRegisterBlock(
    platform: Platform,
    onPlatformSelect: (Platform) -> Unit,
    content: @Composable () -> Unit,
    modifier: Modifier = Modifier,
) {
    DashboardContentBlock(
        title = "Registrar jornada",
        subtitle = "App, horários, km, faturamento e gastos",
        modifier = modifier,
    ) {
        Text(
            text = "Onde você rodou?",
            style = MaterialTheme.typography.labelLarge,
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.padding(bottom = 8.dp),
        )
        JourneyPlatformPicker(
            selected = platform,
            onSelect = onPlatformSelect,
        )
        HorizontalDivider(
            color = Border,
            modifier = Modifier.padding(vertical = 14.dp),
        )
        content()
    }
}
