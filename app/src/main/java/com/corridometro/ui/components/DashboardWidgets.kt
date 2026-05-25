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

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Color

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp

import com.corridometro.domain.ExpenseCategoryTotal

import com.corridometro.domain.Platform

import com.corridometro.domain.Summary

import com.corridometro.ui.theme.AppColors

import com.corridometro.ui.theme.Danger

import com.corridometro.ui.theme.Primary

import com.corridometro.util.formatCurrency

import com.corridometro.util.formatDuration

import com.corridometro.util.formatKm



private data class SummaryMetric(

    val label: String,

    val value: String,

    val valueColor: Color,

)



@Composable

fun SummaryGrid(

    summary: Summary,

    expenseBreakdown: List<ExpenseCategoryTotal> = emptyList(),

    modifier: Modifier = Modifier,

) {

    val onSurface = AppColors.onSurface()

    val expenseMetrics = expenseBreakdown

        .filter { it.total > 0 }

        .map { SummaryMetric(it.category.label, formatCurrency(it.total), Danger) }



    val costMetrics = buildList {

        add(SummaryMetric("Combustível", formatCurrency(summary.fuelCost), Danger))

        addAll(expenseMetrics)

    }



    val performanceMetrics = listOf(

        SummaryMetric("Lucro / km", formatCurrency(summary.profitPerKm), onSurface),

        SummaryMetric("Lucro / hora", formatCurrency(summary.profitPerHour), onSurface),

        SummaryMetric("Km rodados", formatKm(summary.totalKm), onSurface),

        SummaryMetric("Corridas", "${summary.tripCount}", onSurface),

        SummaryMetric("Tempo em jornada", formatDuration(summary.totalMinutes), onSurface),

    )



    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(14.dp)) {

        SummarySectionTitle("Receita")

        SummaryMetricRow(

            metrics = listOf(

                SummaryMetric("Faturamento", formatCurrency(summary.grossEarnings), Primary),

            ),

        )



        SummarySectionTitle("Custos do período")

        if (costMetrics.isEmpty()) {

            SummaryMetricRow(

                metrics = listOf(

                    SummaryMetric("Combustível", formatCurrency(0.0), Danger),

                ),

            )

            Text(

                "Sem gastos extras registrados (almoço, pedágio, etc.).",

                style = MaterialTheme.typography.bodySmall,

                color = AppColors.onSurfaceVariant(),

            )

        } else {

            SummaryMetricRows(metrics = costMetrics)

        }



        SummarySectionTitle("Resultado")

        SummaryMetricRow(

            metrics = listOf(

                SummaryMetric(

                    "Lucro líquido",

                    formatCurrency(summary.profit),

                    if (summary.profit >= 0) Primary else Danger,

                ),

            ),

        )



        SummarySectionTitle("Desempenho")

        SummaryMetricRows(metrics = performanceMetrics)

    }

}



@Composable

private fun SummarySectionTitle(title: String) {

    Text(

        text = title,

        style = MaterialTheme.typography.labelLarge,

        fontWeight = FontWeight.SemiBold,

        color = AppColors.onSurfaceVariant(),

        modifier = Modifier.padding(bottom = 2.dp),

    )

}



@Composable

private fun SummaryMetricRow(

    metrics: List<SummaryMetric>,

    modifier: Modifier = Modifier,

) {

    if (metrics.isEmpty()) return

    if (metrics.size == 1) {

        val metric = metrics.first()

        StatCard(

            label = metric.label,

            value = metric.value,

            modifier = modifier.fillMaxWidth(),

            valueColor = metric.valueColor,

        )

        return

    }

    Row(

        modifier = modifier.fillMaxWidth(),

        horizontalArrangement = Arrangement.spacedBy(10.dp),

    ) {

        metrics.forEach { metric ->

            StatCard(

                label = metric.label,

                value = metric.value,

                modifier = Modifier.weight(1f),

                valueColor = metric.valueColor,

            )

        }

    }

}



@Composable

private fun SummaryMetricRows(metrics: List<SummaryMetric>) {

    metrics.chunked(2).forEach { row ->

        SummaryMetricRow(metrics = row)

    }

}



/** Faturamento bruto por app (rodapé da Início — custos já estão no resumo acima). */

@Composable

fun PlatformGrossEarningsRow(

    platform: Platform,

    grossEarnings: Double,

    shiftCount: Int,

    tripCount: Int,

    modifier: Modifier = Modifier,

) {

    Surface(

        modifier = modifier.fillMaxWidth(),

        shape = MaterialTheme.shapes.medium,

        color = AppColors.surfaceVariant(),

        border = BorderStroke(1.dp, AppColors.outline()),

        shadowElevation = 1.dp,

    ) {

        Row(

            modifier = Modifier

                .fillMaxWidth()

                .padding(14.dp),

            verticalAlignment = Alignment.CenterVertically,

        ) {

            PlatformLogo(platform = platform, size = 40.dp)

            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {

                Text(platform.label, fontWeight = FontWeight.SemiBold, color = AppColors.onSurface())

                Text(

                    "$shiftCount jornada(s) · $tripCount corridas",

                    style = MaterialTheme.typography.bodySmall,

                    color = AppColors.onSurfaceVariant(),

                )

            }

            Column(horizontalAlignment = Alignment.End) {

                Text(

                    formatCurrency(grossEarnings),

                    fontWeight = FontWeight.Bold,

                    color = AppColors.primary(),

                )

                Text(

                    "faturamento bruto",

                    style = MaterialTheme.typography.bodySmall,

                    color = AppColors.onSurfaceVariant(),

                )

            }

        }

    }

}



@Composable

fun PlatformSummaryRow(platform: Platform, summary: Summary, modifier: Modifier = Modifier) {

    Surface(

        modifier = modifier.fillMaxWidth(),

        shape = MaterialTheme.shapes.medium,

        color = AppColors.surfaceVariant(),

        border = BorderStroke(1.dp, AppColors.outline()),

        shadowElevation = 1.dp,

    ) {

        Row(

            modifier = Modifier

                .fillMaxWidth()

                .padding(14.dp),

            verticalAlignment = Alignment.CenterVertically,

        ) {

            PlatformLogo(platform = platform, size = 40.dp)

            Column(modifier = Modifier.weight(1f).padding(horizontal = 12.dp)) {

                Text(platform.label, fontWeight = FontWeight.SemiBold, color = AppColors.onSurface())

                Text(

                    "${summary.shiftCount} jornadas · ${summary.tripCount} corridas · ${formatKm(summary.totalKm)}",

                    style = MaterialTheme.typography.bodySmall,

                    color = AppColors.onSurfaceVariant(),

                )

            }

            Column(horizontalAlignment = Alignment.End) {

                Text(

                    formatCurrency(summary.profit),

                    fontWeight = FontWeight.Bold,

                    color = AppColors.primary(),

                )

                Text(

                    "${formatCurrency(summary.profitPerKm)}/km",

                    style = MaterialTheme.typography.bodySmall,

                    color = AppColors.onSurfaceVariant(),

                )

            }

        }

    }

}


