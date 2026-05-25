package com.corridometro.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.corridometro.domain.ExpenseCategory
import java.time.LocalDate

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun ShiftJourneyForm(
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
    includeExpenses: Boolean = true,
) {
    DateFormField(
        label = "Data da jornada",
        dateText = dateText,
        onDateChange = onDateChange,
    )
    Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
        TimeFormField(
            label = "Início",
            timeText = startTimeText,
            onTimeChange = onStartTimeChange,
            defaultHour = 8,
            defaultMinute = 0,
            modifier = Modifier.weight(1f),
        )
        TimeFormField(
            label = "Fim",
            timeText = endTimeText,
            onTimeChange = onEndTimeChange,
            defaultHour = 18,
            defaultMinute = 0,
            modifier = Modifier.weight(1f),
        )
    }
    FormField(
        "Km rodados",
        kmText,
        onKmChange,
        placeholder = "120,5",
        inputKind = FieldInputKind.Decimal,
    )
    FormField(
        "Consumo do carro (km/L)",
        consumptionText,
        onConsumptionChange,
        placeholder = "12,5",
        inputKind = FieldInputKind.Decimal,
    )
    FormField(
        "Quantidade de corridas",
        tripCountText,
        onTripCountChange,
        placeholder = "15",
        inputKind = FieldInputKind.Number,
    )
    FormField(
        "Preço do combustível (R$/L)",
        fuelPriceText,
        onFuelPriceChange,
        placeholder = "5,89",
        inputKind = FieldInputKind.Decimal,
    )
    FormField(
        "Faturamento total (R$)",
        totalEarningsText,
        onTotalEarningsChange,
        placeholder = "350,00",
        inputKind = FieldInputKind.Decimal,
    )
    FormField("Observação (opcional)", noteText, onNoteChange)
    if (includeExpenses) {
        JourneyExpenseFields(
            amounts = expenseAmounts,
            onAmountChange = onExpenseAmountChange,
            expensesAnchor = expensesAnchor,
        )
    }
}
