package com.corridometro.ui.mockup

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.corridometro.domain.DayHistoryItem
import com.corridometro.domain.Platform
import com.corridometro.domain.PlatformMainApps
import com.corridometro.domain.WorkShift
import com.corridometro.domain.fuelCostForShift
import com.corridometro.domain.netProfitForShift
import com.corridometro.domain.profitPerHourForShift
import com.corridometro.domain.shiftDurationMinutes
import com.corridometro.ui.CorridometroUiState
import com.corridometro.ui.components.PlatformLogo
import com.corridometro.ui.components.ShiftJourneyForm
import com.corridometro.ui.theme.Border
import com.corridometro.ui.theme.Primary
import com.corridometro.ui.theme.PrimarySoft
import com.corridometro.ui.theme.Surface as SurfaceColor
import com.corridometro.ui.theme.TextPrimary
import com.corridometro.ui.theme.TextSecondary
import com.corridometro.util.formatCurrency
import com.corridometro.util.formatDateInput
import com.corridometro.util.formatDuration
import com.corridometro.util.formatKm
import com.corridometro.util.formatTime
import java.time.LocalDate

/** UI do mockup «Jornada» (imagem com blocos numerados). */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MockupJornadaLayout(
    uiState: CorridometroUiState,
    platform: Platform,
    onPlatformSelect: (Platform) -> Unit,
    dateText: String,
    onDateChange: (LocalDate) -> Unit,
    startTimeText: String,
    onStartTimeChange: (String) -> Unit,
    endTimeText: String,
    onEndTimeChange: (String) -> Unit,
    kmText: String,
    onKmChange: (String) -> Unit,
    consumptionText: String,
    onConsumptionChange: (String) -> Unit,
    tripCountText: String,
    onTripCountChange: (String) -> Unit,
    fuelPriceText: String,
    onFuelPriceChange: (String) -> Unit,
    totalEarningsText: String,
    onTotalEarningsChange: (String) -> Unit,
    noteText: String,
    onNoteChange: (String) -> Unit,
    expenseAmounts: Map<com.corridometro.domain.ExpenseCategory, String>,
    onExpenseAmountChange: (com.corridometro.domain.ExpenseCategory, String) -> Unit,
    expensesAnchor: androidx.compose.foundation.relocation.BringIntoViewRequester,
    previewShift: WorkShift?,
    todayHistory: List<DayHistoryItem>,
    onSave: () -> Unit,
    saveEnabled: Boolean,
    workShifts: List<WorkShift>,
    onOpenDayReport: (Long) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showFormDetails by remember { mutableStateOf(false) }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                Text(
                    "Jornada",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                )
                Icon(Icons.Default.HelpOutline, "Ajuda", tint = TextSecondary)
            }
        }

        uiState.journeyMessage?.let { msg ->
            item {
                Surface(color = PrimarySoft, shape = MaterialTheme.shapes.medium) {
                    Text(msg, modifier = Modifier.padding(12.dp), color = Primary, fontWeight = FontWeight.Medium)
                }
            }
        }

        item {
            MockupNumberedBlock(
                number = "1",
                title = "Registrar jornada",
                subtitle = "Selecione a plataforma e informe o período",
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    PlatformMainApps.forEach { p ->
                        MockupPlatformSelectCard(
                            platform = p,
                            selected = platform == p,
                            onClick = { onPlatformSelect(p) },
                            modifier = Modifier.weight(1f),
                        )
                    }
                }
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 14.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    MockupPickerField(
                        icon = Icons.Default.CalendarToday,
                        label = "Data",
                        value = dateText,
                        modifier = Modifier.weight(1f),
                        onClick = { showFormDetails = !showFormDetails },
                    )
                    MockupPickerField(
                        icon = Icons.Default.AccessTime,
                        label = "Horário",
                        value = "$startTimeText - $endTimeText",
                        modifier = Modifier.weight(1f),
                        onClick = { showFormDetails = !showFormDetails },
                    )
                }
                if (showFormDetails) {
                    HorizontalDivider(color = Border, modifier = Modifier.padding(vertical = 12.dp))
                    Text(
                        "Dados da jornada",
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.padding(bottom = 8.dp),
                    )
                    ShiftJourneyForm(
                        dateText = dateText,
                        onDateChange = onDateChange,
                        startTimeText = startTimeText,
                        onStartTimeChange = onStartTimeChange,
                        endTimeText = endTimeText,
                        onEndTimeChange = onEndTimeChange,
                        kmText = kmText,
                        onKmChange = onKmChange,
                        consumptionText = consumptionText,
                        onConsumptionChange = onConsumptionChange,
                        tripCountText = tripCountText,
                        onTripCountChange = onTripCountChange,
                        fuelPriceText = fuelPriceText,
                        onFuelPriceChange = onFuelPriceChange,
                        totalEarningsText = totalEarningsText,
                        onTotalEarningsChange = onTotalEarningsChange,
                        noteText = noteText,
                        onNoteChange = onNoteChange,
                        expenseAmounts = expenseAmounts,
                        onExpenseAmountChange = onExpenseAmountChange,
                        expensesAnchor = expensesAnchor,
                    )
                } else {
                    Text(
                        "Toque em Data ou Horário para preencher km, faturamento e gastos.",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 10.dp),
                    )
                }
            }
        }

        if (previewShift != null) {
            item {
                MockupNumberedBlock(
                    number = "2",
                    title = "Prévia de resultados",
                    subtitle = "Estimativa calculada com base nos seus dados",
                ) {
                    MockupPreviewResults(shift = previewShift)
                }
            }
        }

        item {
            MockupNumberedBlock(
                number = "3",
                title = "Hoje",
                subtitle = "Suas jornadas de hoje",
            ) {
                if (todayHistory.isEmpty()) {
                    Text("Nenhuma jornada salva hoje.", color = TextSecondary, style = MaterialTheme.typography.bodyMedium)
                } else {
                    todayHistory.forEach { day ->
                        MockupTodayJourneyRow(
                            item = day,
                            workShifts = workShifts,
                            onClick = { onOpenDayReport(day.dateEpochDay) },
                        )
                    }
                }
            }
        }

        item {
            Button(
                onClick = onSave,
                enabled = saveEnabled,
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(14.dp),
                colors = ButtonDefaults.buttonColors(containerColor = Primary),
            ) {
                Icon(Icons.Default.Save, null, tint = Color.White, modifier = Modifier.padding(end = 8.dp))
                Text("Salvar jornada", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun MockupNumberedBlock(
    number: String,
    title: String,
    subtitle: String,
    content: @Composable () -> Unit,
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = SurfaceColor,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, Border),
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = "$number. $title",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 14.dp),
            )
            content()
        }
    }
}

@Composable
private fun MockupPlatformSelectCard(
    platform: Platform,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.large,
        color = SurfaceColor,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) Primary else Border,
        ),
    ) {
        Box(modifier = Modifier.padding(12.dp)) {
            if (selected) {
                Icon(
                    Icons.Default.CheckCircle,
                    null,
                    tint = Primary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .size(18.dp),
                )
            }
            Column(
                modifier = Modifier.fillMaxWidth(),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                PlatformLogo(platform = platform, size = 36.dp)
                Text(
                    platform.label,
                    style = MaterialTheme.typography.labelMedium,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(top = 6.dp),
                )
            }
        }
    }
}

@Composable
private fun MockupPickerField(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = Color(0xFFF9FAFB),
        border = BorderStroke(1.dp, Border),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = TextSecondary, modifier = Modifier.size(18.dp))
                Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary, modifier = Modifier.padding(start = 6.dp))
            }
            Text(value, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 6.dp))
        }
    }
}

@Composable
private fun MockupPreviewResults(shift: WorkShift) {
    val profit = netProfitForShift(shift)
    val fuel = fuelCostForShift(shift)
    val minutes = shiftDurationMinutes(shift.startMinutesOfDay, shift.endMinutesOfDay)

    Text("Lucro estimado", style = MaterialTheme.typography.bodySmall, color = TextSecondary)
    Text(
        formatCurrency(profit),
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.Bold,
        color = Primary,
        modifier = Modifier.padding(bottom = 14.dp),
    )
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        MockupPreviewMetric(Icons.Default.Route, "Quilômetros", formatKm(shift.km), Modifier.weight(1f))
        MockupPreviewMetric(Icons.Default.AccessTime, "Horas online", formatDuration(minutes), Modifier.weight(1f))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 10.dp),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        MockupPreviewMetric(Icons.Default.LocalGasStation, "Combustível", formatCurrency(fuel), Modifier.weight(1f))
        MockupPreviewMetric(
            Icons.Default.Wallet,
            "Lucro por hora",
            formatCurrency(profitPerHourForShift(shift)) + "/h",
            Modifier.weight(1f),
        )
    }
}

@Composable
private fun MockupPreviewMetric(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = Color(0xFFF0FDF4),
        border = BorderStroke(1.dp, Primary.copy(alpha = 0.2f)),
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Icon(icon, null, tint = Primary, modifier = Modifier.size(20.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary, modifier = Modifier.padding(top = 6.dp))
            Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        }
    }
}

@Composable
private fun MockupTodayJourneyRow(
    item: DayHistoryItem,
    workShifts: List<WorkShift>,
    onClick: () -> Unit,
) {
    val shift = workShifts.filter { it.dateEpochDay == item.dateEpochDay }.firstOrNull() ?: return
    val profit = netProfitForShift(shift)
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
            .clickable(onClick = onClick),
        shape = MaterialTheme.shapes.medium,
        color = Color(0xFFF9FAFB),
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlatformLogo(platform = shift.platform, size = 32.dp)
            Column(modifier = Modifier.weight(1f).padding(horizontal = 10.dp)) {
                Text(shift.platform.label, fontWeight = FontWeight.SemiBold)
                Text(
                    "${formatTime(shift.startMinutesOfDay)} - ${formatTime(shift.endMinutesOfDay)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                )
            }
            Text(
                "+${formatCurrency(profit)}",
                color = Primary,
                fontWeight = FontWeight.Bold,
            )
            Icon(Icons.Default.ChevronRight, null, tint = TextSecondary, modifier = Modifier.padding(start = 4.dp))
        }
    }
}
