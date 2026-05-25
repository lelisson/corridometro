package com.corridometro.ui.components

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.corridometro.domain.ExpenseCategory
import com.corridometro.domain.JourneyExpenseCategories
import com.corridometro.ui.theme.AppColors

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun JourneyExpenseFields(
    amounts: Map<ExpenseCategory, String>,
    onAmountChange: (ExpenseCategory, String) -> Unit,
    modifier: Modifier = Modifier,
    expensesAnchor: BringIntoViewRequester? = null,
    showHeader: Boolean = true,
) {
    Column(modifier = modifier) {
        if (showHeader) {
            HorizontalDivider(
                color = AppColors.outline(),
                modifier = Modifier.padding(vertical = 12.dp),
            )
            Text(
                text = "Despesas do Dia",
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.SemiBold,
                color = AppColors.onSurface(),
                modifier = Modifier.padding(bottom = 4.dp),
            )
            Text(
                text = "Preencha só o que teve no dia — almoço, pedágio, etc.",
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.onSurfaceVariant(),
                modifier = Modifier.padding(bottom = 8.dp),
            )
        }
        JourneyExpenseCategories.forEachIndexed { index, category ->
            FormField(
                label = category.label,
                value = amounts[category].orEmpty(),
                onValueChange = { onAmountChange(category, it) },
                placeholder = "0,00",
                inputKind = FieldInputKind.Decimal,
                modifier = if (index == 0 && expensesAnchor != null) {
                    Modifier.bringIntoViewRequester(expensesAnchor)
                } else {
                    Modifier
                },
            )
        }
    }
}
