package com.corridometro.ui.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.corridometro.domain.Expense
import com.corridometro.domain.ExpenseCategory
import com.corridometro.domain.JourneyExpenseCategories
import com.corridometro.domain.Platform
import com.corridometro.domain.WorkShift
import com.corridometro.domain.avgEarningPerTrip
import com.corridometro.domain.fuelCostForShift
import com.corridometro.domain.fuelLitersUsed
import com.corridometro.domain.netProfitForShift
import com.corridometro.domain.profitPerHourForShift
import com.corridometro.domain.profitPerKmForShift
import com.corridometro.domain.shiftDurationMinutes
import com.corridometro.domain.toEpochDayLong
import com.corridometro.ui.CorridometroViewModel
import com.corridometro.ui.DesignFeatures
import com.corridometro.ui.mockup.MockupJornadaLayout
import com.corridometro.ui.ads.JourneySavedInterstitialEffect
import com.corridometro.ui.ads.runWithOptionalInterstitial
import java.time.LocalDate
import com.corridometro.ui.components.AddExpensesPromptDialog
import com.corridometro.ui.components.DayHistoryCard
import com.corridometro.ui.components.DuplicateDayDialog
import com.corridometro.ui.components.PlatformLogo
import com.corridometro.ui.components.PrimaryButton
import com.corridometro.ui.components.SecondaryButton
import com.corridometro.ui.components.JourneyPlatformPicker
import com.corridometro.ui.components.SectionTitle
import com.corridometro.ui.components.ShiftJourneyForm
import com.corridometro.ui.components.StatCard
import com.corridometro.ui.theme.Border
import com.corridometro.ui.theme.Danger
import com.corridometro.ui.theme.Primary
import com.corridometro.ui.theme.PrimarySoft
import com.corridometro.ui.theme.Surface as SurfaceColor
import com.corridometro.ui.theme.TextSecondary
import com.corridometro.util.formatAmountInput
import com.corridometro.util.formatConsumption
import com.corridometro.util.formatCurrency
import com.corridometro.util.formatDate
import com.corridometro.util.formatDateInput
import com.corridometro.util.formatDuration
import com.corridometro.util.formatKm
import com.corridometro.util.formatLiters
import com.corridometro.util.formatTime
import com.corridometro.util.parseAmount
import com.corridometro.util.parseDateInput
import com.corridometro.util.parseIntAmount
import com.corridometro.util.parseTime
private fun emptyExpenseAmounts(): Map<ExpenseCategory, String> =
    JourneyExpenseCategories.associateWith { "" }

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShiftScreen(viewModel: CorridometroViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val context = LocalContext.current

    var dateText by rememberSaveable { mutableStateOf(formatDateInput()) }
    var startTimeText by rememberSaveable { mutableStateOf("08:00") }
    var endTimeText by rememberSaveable { mutableStateOf("18:00") }
    var kmText by rememberSaveable { mutableStateOf("") }
    var consumptionText by rememberSaveable { mutableStateOf("") }
    var tripCountText by rememberSaveable { mutableStateOf("") }
    var fuelPriceText by rememberSaveable { mutableStateOf("") }
    var totalEarningsText by rememberSaveable { mutableStateOf("") }
    var noteText by rememberSaveable { mutableStateOf("") }
    var platform by rememberSaveable { mutableStateOf(Platform.UBER) }
    var expenseAmounts by rememberSaveable { mutableStateOf(emptyExpenseAmounts()) }
    var editingShiftId by rememberSaveable { mutableStateOf<Long?>(null) }
    var showReportsBrowser by rememberSaveable { mutableStateOf(false) }

    val selectedDate = parseDateInput(dateText) ?: LocalDate.now()
    val todayEpochDay = LocalDate.now().toEpochDay()
    val todayHistory = uiState.dayHistory.filter { it.dateEpochDay == todayEpochDay }
    val selectedEpochDay = selectedDate.toEpochDayLong()

    JourneySavedInterstitialEffect(
        isPremium = uiState.isPremium,
        journeySavedAdSignal = uiState.journeySavedAdSignal,
    )

    val startMinutes = parseTime(startTimeText)
    val endMinutes = parseTime(endTimeText)
    val km = parseAmount(kmText)
    val fuelKmPerLiter = parseAmount(consumptionText)
    val tripCount = parseIntAmount(tripCountText)
    val fuelPrice = parseAmount(fuelPriceText)
    val totalEarnings = parseAmount(totalEarningsText)

    val previewShift = if (
        startMinutes != null &&
        endMinutes != null &&
        km > 0 &&
        fuelKmPerLiter > 0 &&
        tripCount > 0 &&
        fuelPrice > 0 &&
        totalEarnings > 0
    ) {
        WorkShift(
            platform = platform,
            dateEpochDay = selectedEpochDay,
            startMinutesOfDay = startMinutes,
            endMinutesOfDay = endMinutes,
            km = km,
            fuelKmPerLiter = fuelKmPerLiter,
            tripCount = tripCount,
            fuelPricePerLiter = fuelPrice,
            totalEarnings = totalEarnings,
        )
    } else {
        null
    }

    val expensesBringIntoView = remember { BringIntoViewRequester() }

    LaunchedEffect(uiState.editingShift) {
        val shift = uiState.editingShift ?: return@LaunchedEffect
        editingShiftId = shift.id
        platform = shift.platform
        dateText = formatDateInput(java.time.LocalDate.ofEpochDay(shift.dateEpochDay))
        startTimeText = formatTime(shift.startMinutesOfDay)
        endTimeText = formatTime(shift.endMinutesOfDay)
        kmText = formatAmountInput(shift.km)
        consumptionText = formatAmountInput(shift.fuelKmPerLiter)
        tripCountText = shift.tripCount.toString()
        fuelPriceText = formatAmountInput(shift.fuelPricePerLiter)
        totalEarningsText = formatAmountInput(shift.totalEarnings)
        noteText = shift.note.orEmpty()
        expenseAmounts = JourneyExpenseCategories.associateWith { category ->
            val total = uiState.expenses
                .filter {
                    it.dateEpochDay == shift.dateEpochDay &&
                        it.category == category &&
                        (it.platform == null || it.platform == shift.platform)
                }
                .sumOf { it.amount }
            formatAmountInput(total)
        }
        viewModel.clearEditingShift()
    }

    uiState.duplicateDayPrompt?.let { prompt ->
        DuplicateDayDialog(
            prompt = prompt,
            onDismiss = viewModel::dismissDuplicateDayPrompt,
            onConfirm = viewModel::confirmSaveWorkShift,
        )
    }

    uiState.addExpensesPromptDay?.let { day ->
        AddExpensesPromptDialog(
            dateEpochDay = day,
            onAddExpenses = { viewModel.confirmAddExpenses(true) },
            onSkip = { viewModel.confirmAddExpenses(false) },
        )
    }

    LaunchedEffect(uiState.scrollToExpensesSignal) {
        if (uiState.scrollToExpensesSignal > 0) {
            expensesBringIntoView.bringIntoView()
        }
    }

    LaunchedEffect(uiState.journeyMessage) {
        if (uiState.journeyMessage != null) {
            kotlinx.coroutines.delay(3500)
            viewModel.clearJourneyMessage()
        }
    }

    val saveJourney: () -> Unit = saveJourney@{
        val shift = previewShift ?: return@saveJourney
        val dayExpenses = JourneyExpenseCategories.mapNotNull { category ->
            val amount = parseAmount(expenseAmounts[category].orEmpty())
            if (amount <= 0) return@mapNotNull null
            Expense(
                category = category,
                amount = amount,
                dateEpochDay = selectedEpochDay,
                platform = platform,
            )
        }
        val shiftToSave = shift.copy(
            id = editingShiftId ?: 0L,
            note = noteText.ifBlank { null },
        )
        if (editingShiftId != null) {
            viewModel.updateWorkShift(shiftToSave, dayExpenses)
            editingShiftId = null
        } else {
            viewModel.requestSaveWorkShift(shiftToSave, dayExpenses)
        }
        kmText = ""
        consumptionText = ""
        tripCountText = ""
        fuelPriceText = ""
        totalEarningsText = ""
        noteText = ""
        expenseAmounts = emptyExpenseAmounts()
    }

    val formContent: @Composable () -> Unit = {
        ShiftJourneyForm(
            dateText = dateText,
            onDateChange = { dateText = formatDateInput(it) },
            startTimeText = startTimeText,
            onStartTimeChange = { startTimeText = it },
            endTimeText = endTimeText,
            onEndTimeChange = { endTimeText = it },
            kmText = kmText,
            onKmChange = { kmText = it },
            consumptionText = consumptionText,
            onConsumptionChange = { consumptionText = it },
            tripCountText = tripCountText,
            onTripCountChange = { tripCountText = it },
            fuelPriceText = fuelPriceText,
            onFuelPriceChange = { fuelPriceText = it },
            totalEarningsText = totalEarningsText,
            onTotalEarningsChange = { totalEarningsText = it },
            noteText = noteText,
            onNoteChange = { noteText = it },
            expenseAmounts = expenseAmounts,
            onExpenseAmountChange = { category, value ->
                expenseAmounts = expenseAmounts.toMutableMap().apply { put(category, value) }
            },
            expensesAnchor = expensesBringIntoView,
        )
    }

    Box(modifier = Modifier.fillMaxSize()) {
    if (DesignFeatures.isMockupJornada) {
        MockupJornadaLayout(
            uiState = uiState,
            platform = platform,
            onPlatformSelect = { platform = it },
            dateText = dateText,
            onDateChange = { dateText = formatDateInput(it) },
            startTimeText = startTimeText,
            onStartTimeChange = { startTimeText = it },
            endTimeText = endTimeText,
            onEndTimeChange = { endTimeText = it },
            kmText = kmText,
            onKmChange = { kmText = it },
            consumptionText = consumptionText,
            onConsumptionChange = { consumptionText = it },
            tripCountText = tripCountText,
            onTripCountChange = { tripCountText = it },
            fuelPriceText = fuelPriceText,
            onFuelPriceChange = { fuelPriceText = it },
            totalEarningsText = totalEarningsText,
            onTotalEarningsChange = { totalEarningsText = it },
            noteText = noteText,
            onNoteChange = { noteText = it },
            expenseAmounts = expenseAmounts,
            onExpenseAmountChange = { category, value ->
                expenseAmounts = expenseAmounts.toMutableMap().apply { put(category, value) }
            },
            expensesAnchor = expensesBringIntoView,
            previewShift = previewShift,
            todayHistory = todayHistory,
            onSave = saveJourney,
            saveEnabled = previewShift != null,
            workShifts = uiState.workShifts,
            onOpenDayReport = viewModel::openDayReport,
        )
    } else {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            SectionTitle("Sua jornada")
            Text(
                "Registre o dia e os gastos no mesmo formulário.",
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
        }

        uiState.journeyMessage?.let { message ->
            item {
                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    color = PrimarySoft,
                    shape = MaterialTheme.shapes.medium,
                    border = BorderStroke(1.dp, Primary.copy(alpha = 0.3f)),
                ) {
                    Text(
                        message,
                        modifier = Modifier.padding(12.dp),
                        color = Primary,
                        fontWeight = FontWeight.Medium,
                    )
                }
            }
        }

        item {
            Text(
                "Onde você rodou?",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier.padding(bottom = 8.dp),
            )
            JourneyPlatformPicker(
                selected = platform,
                onSelect = { platform = it },
            )
        }
        item {
            Surface(
                shape = MaterialTheme.shapes.large,
                color = SurfaceColor,
                shadowElevation = 1.dp,
                border = BorderStroke(1.dp, Border),
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    SectionTitle("Insira sua jornada")
                    formContent()
                }
            }
        }

        previewShift?.let { shift ->
            item { ShiftPreviewCard(shift) }
        }

        item {
            PrimaryButton(
                text = if (editingShiftId != null) "Atualizar jornada" else "Salvar jornada",
                onClick = saveJourney,
                enabled = previewShift != null,
            )
        }

        item {
            HorizontalDivider(color = Border, modifier = Modifier.padding(vertical = 4.dp))
            SectionTitle("Histórico do dia")
            Text(
                "Somente a jornada de hoje. Para outros dias, use o consultor abaixo.",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
                modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
            )
            SecondaryButton(
                text = if (uiState.dayHistory.isEmpty()) {
                    "Consultar relatórios"
                } else {
                    "Consultar todos os relatórios (${uiState.dayHistory.size})"
                },
                onClick = {
                    runWithOptionalInterstitial(context, uiState.isPremium) {
                        showReportsBrowser = true
                    }
                },
                enabled = uiState.dayHistory.isNotEmpty(),
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
        if (todayHistory.isEmpty()) {
            item {
                Text(
                    if (uiState.dayHistory.isEmpty()) {
                        "Nenhuma jornada salva ainda."
                    } else {
                        "Nenhuma jornada registrada hoje."
                    },
                    style = MaterialTheme.typography.bodyMedium,
                    color = TextSecondary,
                )
            }
        } else {
            items(todayHistory, key = { it.dateEpochDay }) { dayItem ->
                DayHistoryCard(
                    item = dayItem,
                    onOpenReport = { viewModel.openDayReport(dayItem.dateEpochDay) },
                )
            }
        }
    }
    }

        if (showReportsBrowser) {
            DayReportsBrowserScreen(
                days = uiState.dayHistory,
                onOpenReport = { day ->
                    viewModel.openDayReport(day)
                    showReportsBrowser = false
                },
                onClose = { showReportsBrowser = false },
            )
        }
    }
}

@Composable
internal fun ShiftPreviewCard(shift: WorkShift) {
    val fuelCost = fuelCostForShift(shift)
    val profit = netProfitForShift(shift)
    val minutes = shiftDurationMinutes(shift.startMinutesOfDay, shift.endMinutesOfDay)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = SurfaceColor,
        border = BorderStroke(1.dp, Border),
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp),
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                PlatformLogo(platform = shift.platform, size = 36.dp)
                Column(modifier = Modifier.padding(start = 10.dp)) {
                    Text("Resumo da jornada", fontWeight = FontWeight.Bold)
                    Text(
                        formatDate(shift.dateEpochDay),
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                    )
                }
            }
            Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                StatCard(
                    label = "Combustível",
                    value = formatCurrency(fuelCost),
                    modifier = Modifier.weight(1f),
                    valueColor = Danger,
                )
                StatCard(
                    label = "Lucro (antes de outros gastos)",
                    value = formatCurrency(profit),
                    modifier = Modifier.weight(1f),
                    valueColor = if (profit >= 0) Primary else Danger,
                )
            }
            Text(
                "${formatLiters(fuelLitersUsed(shift.km, shift.fuelKmPerLiter))} · " +
                    "${formatCurrency(profitPerKmForShift(shift))}/km · " +
                    "${formatCurrency(profitPerHourForShift(shift))}/h · " +
                    formatDuration(minutes),
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
            Text(
                "Média por corrida: ${formatCurrency(avgEarningPerTrip(shift))}",
                style = MaterialTheme.typography.bodySmall,
                color = TextSecondary,
            )
        }
    }
}

@Composable
internal fun ShiftRow(
    shift: WorkShift,
    onEdit: () -> Unit = {},
    onDelete: () -> Unit = {},
    showActions: Boolean = true,
) {
    val profit = netProfitForShift(shift)
    val minutes = shiftDurationMinutes(shift.startMinutesOfDay, shift.endMinutesOfDay)

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.medium,
        color = com.corridometro.ui.theme.AppColors.surfaceVariant(),
        border = BorderStroke(1.dp, com.corridometro.ui.theme.AppColors.outline()),
        shadowElevation = 1.dp,
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            PlatformLogo(platform = shift.platform, size = 40.dp)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(horizontal = 12.dp),
            ) {
                Text(
                    shift.platform.label,
                    fontWeight = FontWeight.SemiBold,
                    color = com.corridometro.ui.theme.AppColors.onSurface(),
                )
                Text(
                    "${formatDate(shift.dateEpochDay)} · ${formatTime(shift.startMinutesOfDay)} – ${formatTime(shift.endMinutesOfDay)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = com.corridometro.ui.theme.AppColors.onSurfaceVariant(),
                )
                Text(
                    "${shift.tripCount} corridas · ${formatKm(shift.km)} · ${formatConsumption(shift.fuelKmPerLiter)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = com.corridometro.ui.theme.AppColors.onSurfaceVariant(),
                )
                Text(
                    "Combustível ${formatCurrency(fuelCostForShift(shift))} · ${formatDuration(minutes)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = com.corridometro.ui.theme.AppColors.onSurfaceVariant(),
                )
            }
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    formatCurrency(shift.totalEarnings),
                    fontWeight = FontWeight.Bold,
                    color = com.corridometro.ui.theme.AppColors.primary(),
                )
                Text(
                    "Lucro ${formatCurrency(profit)}",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (profit >= 0) com.corridometro.ui.theme.AppColors.primary() else Danger,
                )
            }
            if (showActions) {
                IconButton(onClick = onEdit) {
                    Icon(
                        Icons.Default.Edit,
                        contentDescription = "Editar",
                        tint = com.corridometro.ui.theme.AppColors.primary(),
                    )
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = "Excluir", tint = Danger)
                }
            }
        }
    }
}
