package com.corridometro.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.corridometro.domain.DayReport
import com.corridometro.domain.WorkShift
import com.corridometro.ui.screens.ShiftRow
import com.corridometro.ui.theme.AppColors
import com.corridometro.util.formatDate
import java.time.Instant
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.Locale

private val br = Locale.forLanguageTag("pt-BR")
private val dateTimeFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm", br)

@Composable
fun DayReportContent(
    report: DayReport,
    modifier: Modifier = Modifier,
    showShiftDetails: Boolean = true,
    onEditShift: (WorkShift) -> Unit = {},
    onDeleteShift: (WorkShift) -> Unit = {},
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        item {
            Text(
                text = "Relatório — ${formatDate(report.dateEpochDay)}",
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = FontWeight.Bold,
                color = AppColors.onSurface(),
            )
            if (report.isFinalized && report.finalizedAtEpochMillis != null) {
                val whenText = Instant.ofEpochMilli(report.finalizedAtEpochMillis)
                    .atZone(ZoneId.systemDefault())
                    .format(dateTimeFormat)
                Text(
                    "Finalizado em $whenText",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.primary(),
                )
            } else {
                Text(
                    "Prévia do dia (ainda não finalizado)",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.onSurfaceVariant(),
                )
            }
        }

        item {
            SummaryGrid(
                summary = report.summary,
                expenseBreakdown = report.expenseBreakdown,
            )
        }

        if (report.platformBreakdown.isNotEmpty()) {
            item {
                SectionTitle("Total por aplicativo")
            }
            items(report.platformBreakdown, key = { it.first.name }) { (platform, summary) ->
                PlatformSummaryRow(platform, summary)
            }
        }

        if (showShiftDetails && report.shifts.isNotEmpty()) {
            item {
                SectionTitle("Jornadas registradas")
            }
            items(report.shifts, key = { "${it.id}-${it.platform.name}" }) { shift ->
                ShiftRow(
                    shift = shift,
                    onEdit = { onEditShift(shift) },
                    onDelete = { onDeleteShift(shift) },
                    showActions = true,
                )
            }
        }
    }
}
