package com.corridometro.ui.mockup.appa

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.corridometro.domain.Expense
import com.corridometro.domain.ExpenseCategory
import com.corridometro.domain.JourneyExpenseCategories
import com.corridometro.domain.Platform
import com.corridometro.domain.WorkShift
import com.corridometro.domain.toEpochDayLong
import com.corridometro.ui.CorridometroViewModel
import com.corridometro.ui.ads.JourneySavedInterstitialEffect
import com.corridometro.ui.components.AddExpensesPromptDialog
import com.corridometro.ui.components.DuplicateDayDialog
import com.corridometro.util.formatAmountInput
import com.corridometro.util.formatDateInput
import com.corridometro.util.formatTime
import com.corridometro.util.parseAmount
import com.corridometro.util.parseDateInput
import com.corridometro.util.parseIntAmount
import com.corridometro.util.parseTime
import java.time.LocalDate
import kotlinx.coroutines.delay

private fun emptyExpenseAmounts(): Map<ExpenseCategory, String> =
    JourneyExpenseCategories.associateWith { "" }

/** Formulário «Nova jornada» em tela cheia (overlay global do APK A). */
@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AppAJourneyFormOverlay(
    viewModel: CorridometroViewModel,
    onClose: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()

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

    val selectedDate = parseDateInput(dateText) ?: LocalDate.now()
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
        dateText = formatDateInput(LocalDate.ofEpochDay(shift.dateEpochDay))
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
            delay(3500)
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
        onClose()
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = { Text("Nova jornada", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Fechar")
                    }
                },
            )
        },
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(14.dp),
        ) {
            item {
                AppAJourneyFormBlocks(
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
                    onSave = saveJourney,
                    saveEnabled = previewShift != null,
                )
            }
        }
    }
}
