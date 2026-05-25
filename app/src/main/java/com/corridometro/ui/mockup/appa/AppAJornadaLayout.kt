package com.corridometro.ui.mockup.appa

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.LocalGasStation
import androidx.compose.material.icons.filled.Route
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Wallet
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import com.corridometro.domain.ExpenseCategory
import com.corridometro.domain.Platform
import com.corridometro.domain.WorkShift
import com.corridometro.domain.fuelCostForShift
import com.corridometro.domain.netProfitForShift
import com.corridometro.domain.profitPerHourForShift
import com.corridometro.domain.shiftDurationMinutes
import com.corridometro.ui.CorridometroUiState
import com.corridometro.ui.components.DashboardContentBlock
import com.corridometro.ui.components.DayHistoryCard
import com.corridometro.ui.components.JourneyExpenseFields
import com.corridometro.ui.components.PrimaryButton
import com.corridometro.ui.components.ShiftJourneyForm
import com.corridometro.ui.theme.AppColors
import com.corridometro.ui.theme.Border
import com.corridometro.ui.theme.Primary
import com.corridometro.ui.theme.PrimarySoft
import com.corridometro.ui.theme.TextSecondary
import com.corridometro.util.formatCurrency
import com.corridometro.util.formatDuration
import com.corridometro.util.formatKm
import com.corridometro.util.formatTime
import java.time.LocalDate

/** Formulário em blocos Material (tela «Nova jornada» do APK A). */
@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppAJourneyFormBlocks(
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
    expenseAmounts: Map<ExpenseCategory, String>,
    onExpenseAmountChange: (ExpenseCategory, String) -> Unit,
    expensesAnchor: BringIntoViewRequester,
    previewShift: WorkShift?,
    onSave: () -> Unit,
    saveEnabled: Boolean,
) {
    var showDayExpenses by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        uiState.journeyMessage?.let { msg ->
            Surface(color = AppColors.primaryContainer(), shape = MaterialTheme.shapes.medium) {
                Text(
                    msg,
                    modifier = Modifier.padding(12.dp),
                    color = AppColors.onPrimaryContainer(),
                    fontWeight = FontWeight.Medium,
                )
            }
        }

        DashboardContentBlock(
            title = "Aplicativo",
            subtitle = "Onde você rodou hoje",
        ) {
            AppAPlatformPicker(selected = platform, onSelect = onPlatformSelect)
        }

        DashboardContentBlock(
            title = "Dados da jornada",
            subtitle = "Data, horários, km e faturamento",
        ) {
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
                includeExpenses = false,
            )
        }

        DashboardContentBlock(
            title = "Despesas do dia",
            subtitle = null,
        ) {
            Button(
                onClick = { showDayExpenses = !showDayExpenses },
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = AppColors.primary(),
                    contentColor = AppColors.onPrimary(),
                ),
            ) {
                Text(
                    text = "Adicionar dia",
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.onPrimary(),
                )
            }
            if (showDayExpenses) {
                JourneyExpenseFields(
                    amounts = expenseAmounts,
                    onAmountChange = onExpenseAmountChange,
                    expensesAnchor = expensesAnchor,
                    showHeader = false,
                    modifier = Modifier.padding(top = 12.dp),
                )
            }
        }

        previewShift?.let { shift ->
            DashboardContentBlock(
                title = "Prévia",
                subtitle = "Atualiza enquanto você digita",
            ) {
                AppAPreviewGrid(shift = shift)
            }
        }

        PrimaryButton(
            text = "Salvar jornada",
            onClick = onSave,
            enabled = saveEnabled,
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun AppAJornadaLayout(
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
    expenseAmounts: Map<ExpenseCategory, String>,
    onExpenseAmountChange: (ExpenseCategory, String) -> Unit,
    expensesAnchor: BringIntoViewRequester,
    previewShift: WorkShift?,
    todayHistory: List<DayHistoryItem>,
    onSave: () -> Unit,
    saveEnabled: Boolean,
    onOpenDayReport: (Long) -> Unit,
    onBrowseReports: () -> Unit,
) {
    var formExpanded by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onSave,
                containerColor = Primary,
                elevation = FloatingActionButtonDefaults.elevation(defaultElevation = 6.dp),
                modifier = Modifier.padding(bottom = 8.dp),
            ) {
                Icon(Icons.Default.Save, "Salvar jornada", tint = Color.White)
            }
        },
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(start = 16.dp, end = 16.dp, top = 8.dp, bottom = 88.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item(key = "header") {
                Text(
                    "Sua jornada",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "Registre corridas e entregas no mesmo fluxo.",
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }

            uiState.journeyMessage?.let { msg ->
                item(key = "msg") {
                    Surface(color = PrimarySoft, shape = MaterialTheme.shapes.medium) {
                        Text(
                            msg,
                            modifier = Modifier.padding(12.dp),
                            color = Primary,
                            fontWeight = FontWeight.Medium,
                        )
                    }
                }
            }

            item(key = "block1") {
                DashboardContentBlock(
                    title = "1 · Registrar jornada",
                    subtitle = "Escolha o app e informe o período",
                ) {
                    AppAPlatformPicker(
                        selected = platform,
                        onSelect = onPlatformSelect,
                    )
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 14.dp),
                        horizontalArrangement = Arrangement.spacedBy(10.dp),
                    ) {
                        AppADateTimeChip(
                            icon = Icons.Default.CalendarToday,
                            label = "Data",
                            value = dateText,
                            onClick = { formExpanded = true },
                            modifier = Modifier.weight(1f),
                        )
                        AppADateTimeChip(
                            icon = Icons.Default.AccessTime,
                            label = "Horário",
                            value = "$startTimeText – $endTimeText",
                            onClick = { formExpanded = true },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 10.dp)
                            .appATouch(
                                onClick = { formExpanded = !formExpanded },
                                label = if (formExpanded) "Recolher formulário" else "Expandir formulário",
                            ),
                        shape = MaterialTheme.shapes.medium,
                        color = Color(0xFFF9FAFB),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Border),
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically,
                        ) {
                            Text(
                                if (formExpanded) "Ocultar km, faturamento e gastos" else "Preencher km, faturamento e gastos",
                                modifier = Modifier.weight(1f),
                                fontWeight = FontWeight.Medium,
                            )
                            Icon(
                                if (formExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                contentDescription = null,
                                tint = Primary,
                            )
                        }
                    }
                    Column(modifier = Modifier.appAAnimateSize()) {
                        AppAExpandable(visible = formExpanded) {
                            Column {
                                HorizontalDivider(
                                    color = Border,
                                    modifier = Modifier.padding(vertical = 12.dp),
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
                            }
                        }
                    }
                }
            }

            if (previewShift != null) {
                item(key = "block2") {
                    DashboardContentBlock(
                        title = "2 · Prévia ao vivo",
                        subtitle = "Atualiza enquanto você digita",
                    ) {
                        AppAPreviewGrid(shift = previewShift)
                    }
                }
            }

            item(key = "block3") {
                DashboardContentBlock(
                    title = "3 · Hoje",
                    subtitle = "Resumo das jornadas do dia",
                ) {
                    if (todayHistory.isEmpty()) {
                        Text(
                            "Nenhuma jornada salva hoje. Use o botão verde para salvar.",
                            color = TextSecondary,
                            style = MaterialTheme.typography.bodyMedium,
                        )
                    } else {
                        todayHistory.forEach { day ->
                            DayHistoryCard(
                                item = day,
                                onOpenReport = { onOpenDayReport(day.dateEpochDay) },
                                modifier = Modifier.padding(bottom = 8.dp),
                            )
                        }
                    }
                    if (uiState.dayHistory.isNotEmpty()) {
                        Button(
                            onClick = onBrowseReports,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(top = 8.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = Primary),
                        ) {
                            Text("Ver todos os relatórios")
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AppADateTimeChip(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.appATouch(onClick = onClick, label = label),
        shape = MaterialTheme.shapes.medium,
        color = Color(0xFFF9FAFB),
        border = androidx.compose.foundation.BorderStroke(1.dp, Border),
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(icon, null, tint = TextSecondary, modifier = Modifier.padding(end = 6.dp))
                Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
            }
            Text(value, fontWeight = FontWeight.SemiBold, modifier = Modifier.padding(top = 4.dp))
        }
    }
}

@Composable
internal fun AppAPreviewGrid(shift: WorkShift) {
    val profit = netProfitForShift(shift)
    val fuel = fuelCostForShift(shift)
    val minutes = shiftDurationMinutes(shift.startMinutesOfDay, shift.endMinutesOfDay)

    Text("Lucro estimado", style = MaterialTheme.typography.bodySmall, color = AppColors.onSurfaceVariant())
    Text(
        formatCurrency(profit),
        style = MaterialTheme.typography.displaySmall,
        fontWeight = FontWeight.Bold,
        color = AppColors.primary(),
        modifier = Modifier.padding(bottom = 12.dp),
    )
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
        AppAPreviewTile(Icons.Default.Route, "Km", formatKm(shift.km), Modifier.weight(1f))
        AppAPreviewTile(Icons.Default.AccessTime, "Online", formatDuration(minutes), Modifier.weight(1f))
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        AppAPreviewTile(Icons.Default.LocalGasStation, "Combustível", formatCurrency(fuel), Modifier.weight(1f))
        AppAPreviewTile(
            Icons.Default.Wallet,
            "R\$/hora",
            formatCurrency(profitPerHourForShift(shift)),
            Modifier.weight(1f),
        )
    }
}

@Composable
private fun AppAPreviewTile(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    value: String,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier,
        shape = MaterialTheme.shapes.medium,
        color = AppColors.primaryContainer(),
        border = androidx.compose.foundation.BorderStroke(1.dp, AppColors.primary().copy(alpha = 0.25f)),
    ) {
        Column(modifier = Modifier.padding(10.dp)) {
            Icon(icon, null, tint = AppColors.primary(), modifier = Modifier.padding(bottom = 4.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = AppColors.onSurfaceVariant())
            Text(value, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.bodyMedium)
        }
    }
}
