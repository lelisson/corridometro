package com.corridometro.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.corridometro.domain.ExpenseCategory
import com.corridometro.domain.JourneyExpenseCategories
import com.corridometro.domain.PeriodFilter
import com.corridometro.ui.theme.Border
import com.corridometro.ui.theme.AppColors
import com.corridometro.ui.theme.Primary
import com.corridometro.ui.theme.Surface as SurfaceColor
import com.corridometro.ui.theme.TextPrimary
import com.corridometro.ui.theme.TextSecondary

enum class FieldInputKind {
    Text,
    Number,
    Decimal,
    Time,
}

@Composable
fun SectionTitle(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        fontWeight = FontWeight.Bold,
        color = AppColors.onSurface(),
        modifier = modifier.padding(bottom = 8.dp),
    )
}

@Composable
fun StatCard(
    label: String,
    value: String,
    modifier: Modifier = Modifier,
    valueColor: Color = Color.Unspecified,
) {
    val resolvedValueColor = if (valueColor == Color.Unspecified) AppColors.onSurface() else valueColor
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = AppColors.surfaceVariant(),
        border = BorderStroke(1.dp, AppColors.outline()),
        shadowElevation = 1.dp,
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(text = label, style = MaterialTheme.typography.labelMedium, color = AppColors.onSurfaceVariant())
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = resolvedValueColor,
                modifier = Modifier.padding(top = 4.dp),
            )
        }
    }
}

@Composable
fun PrimaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.primary(),
            contentColor = AppColors.onPrimary(),
            disabledContainerColor = AppColors.surfaceVariant(),
            disabledContentColor = AppColors.onSurfaceVariant(),
        ),
    ) {
        Text(text, fontWeight = FontWeight.SemiBold, color = if (enabled) AppColors.onPrimary() else AppColors.onSurfaceVariant())
    }
}

@Composable
fun SecondaryButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        modifier = modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = AppColors.primary(),
            contentColor = AppColors.onPrimary(),
            disabledContainerColor = AppColors.surfaceVariant(),
            disabledContentColor = AppColors.onSurfaceVariant(),
        ),
    ) {
        Text(text, color = AppColors.onPrimary(), fontWeight = FontWeight.SemiBold)
    }
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PeriodPicker(
    selected: PeriodFilter,
    onSelect: (PeriodFilter) -> Unit,
    onOpenCalendar: () -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        PeriodFilter.entries.forEach { period ->
            when (period) {
                PeriodFilter.PERSONALIZADO -> {
                    FilterChip(
                        selected = selected == period,
                        onClick = onOpenCalendar,
                        label = { Text(period.label) },
                        colors = periodChipColors(selected == period),
                        border = periodChipBorder(selected == period),
                    )
                }
                PeriodFilter.TUDO -> {
                    FilterChip(
                        selected = selected == period,
                        onClick = { onSelect(period) },
                        label = { Text(period.label) },
                        colors = periodChipColors(selected == period),
                        border = periodChipBorder(selected == period),
                    )
                }
                else -> {
                    FilterChip(
                        selected = selected == period,
                        onClick = { onSelect(period) },
                        label = { Text(period.label) },
                        colors = periodChipColors(selected == period),
                        border = periodChipBorder(selected == period),
                    )
                }
            }
        }
    }
}

@Composable
private fun periodChipColors(selected: Boolean) = FilterChipDefaults.filterChipColors(
    selectedContainerColor = Primary,
    selectedLabelColor = Color.White,
    containerColor = SurfaceColor,
    labelColor = TextPrimary,
)

@Composable
private fun periodChipBorder(selected: Boolean) = FilterChipDefaults.filterChipBorder(
    enabled = true,
    selected = selected,
    borderColor = Border,
    selectedBorderColor = Primary,
)

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun ExpenseCategoryPicker(
    selected: ExpenseCategory,
    onSelect: (ExpenseCategory) -> Unit,
    modifier: Modifier = Modifier,
) {
    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        JourneyExpenseCategories.forEach { category ->
            FilterChip(
                selected = selected == category,
                onClick = { onSelect(category) },
                label = { Text(category.label, style = MaterialTheme.typography.labelLarge) },
                colors = FilterChipDefaults.filterChipColors(
                    selectedContainerColor = Primary,
                    selectedLabelColor = Color.White,
                    containerColor = SurfaceColor,
                    labelColor = TextPrimary,
                ),
                border = FilterChipDefaults.filterChipBorder(
                    enabled = true,
                    selected = selected == category,
                    borderColor = Border,
                    selectedBorderColor = Primary,
                ),
            )
        }
    }
}

@Composable
fun FormField(
    label: String,
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    placeholder: String = "",
    inputKind: FieldInputKind = FieldInputKind.Text,
) {
    val keyboardType = when (inputKind) {
        FieldInputKind.Text -> KeyboardType.Text
        FieldInputKind.Number -> KeyboardType.Number
        FieldInputKind.Decimal, FieldInputKind.Time -> KeyboardType.Decimal
    }

    Column(modifier = modifier.padding(bottom = 12.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            modifier = Modifier.fillMaxWidth(),
            placeholder = {
                Text(placeholder, color = MaterialTheme.colorScheme.onSurfaceVariant)
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = com.corridometro.ui.theme.AppColors.formFieldColors(),
        )
    }
}
