package com.corridometro.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.corridometro.domain.ExpenseCategoryTotal
import com.corridometro.ui.theme.Border
import com.corridometro.ui.theme.Danger
import com.corridometro.ui.theme.Surface as SurfaceColor
import com.corridometro.ui.theme.TextSecondary
import com.corridometro.util.formatCurrency
import com.corridometro.util.formatDate

@Composable
fun ExpenseBreakdownSection(
    breakdown: List<ExpenseCategoryTotal>,
    modifier: Modifier = Modifier,
) {
    if (breakdown.isEmpty()) return

    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(10.dp)) {
        breakdown.forEach { group ->
            Surface(
                modifier = Modifier.fillMaxWidth(),
                shape = MaterialTheme.shapes.medium,
                color = SurfaceColor,
                border = BorderStroke(1.dp, Border),
                shadowElevation = 1.dp,
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                    ) {
                        Text(
                            group.category.label,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleSmall,
                        )
                        Text(
                            formatCurrency(group.total),
                            fontWeight = FontWeight.Bold,
                            color = Danger,
                        )
                    }
                    Text(
                        "${group.count} registro(s)",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary,
                        modifier = Modifier.padding(bottom = 6.dp),
                    )
                    group.items.forEach { expense ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 2.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                        ) {
                            Text(
                                formatDate(expense.dateEpochDay),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                            )
                            Text(
                                formatCurrency(expense.amount),
                                style = MaterialTheme.typography.bodySmall,
                                color = TextSecondary,
                            )
                        }
                    }
                }
            }
        }
    }
}
