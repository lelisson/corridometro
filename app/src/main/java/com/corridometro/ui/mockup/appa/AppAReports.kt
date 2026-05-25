package com.corridometro.ui.mockup.appa



import androidx.activity.compose.BackHandler

import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.lazy.items

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.automirrored.filled.ArrowBack

import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.Icon

import androidx.compose.material3.IconButton

import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Scaffold

import androidx.compose.material3.Text

import androidx.compose.material3.TopAppBar

import androidx.compose.material3.TopAppBarDefaults

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp

import com.corridometro.domain.PeriodReport

import com.corridometro.domain.Platform

import com.corridometro.domain.Summary

import com.corridometro.domain.ExpenseCategoryTotal

import com.corridometro.ui.components.DashboardContentBlock

import com.corridometro.ui.components.PlatformSummaryRow

import com.corridometro.ui.components.SummaryGrid

import com.corridometro.ui.theme.AppColors

import com.corridometro.util.formatCurrency



sealed class AppAReportOverlay {

    data object ProfitOverview : AppAReportOverlay()

    data object Revenue : AppAReportOverlay()

    data object Costs : AppAReportOverlay()

    data object ProfitPerKm : AppAReportOverlay()

    data class PlatformEarnings(val platform: Platform) : AppAReportOverlay()

    data class PeriodHistory(val report: PeriodReport) : AppAReportOverlay()

}



@Composable

fun AppAReportHost(

    overlay: AppAReportOverlay?,

    summary: Summary,

    expenseBreakdown: List<ExpenseCategoryTotal>,

    platformBreakdown: List<Pair<Platform, Summary>>,

    onClose: () -> Unit,

) {

    if (overlay == null) return

    BackHandler(onBack = onClose)

    when (overlay) {

        AppAReportOverlay.ProfitOverview -> AppAProfitOverviewReport(

            summary = summary,

            expenseBreakdown = expenseBreakdown,

            platformBreakdown = platformBreakdown,

            onClose = onClose,

        )

        AppAReportOverlay.Revenue -> AppAMetricReport(

            title = "Faturamento",

            subtitle = "Total bruto no período selecionado",

            summary = summary.copy(

                profit = summary.grossEarnings,

                expenses = 0.0,

                fuelCost = 0.0,

                otherExpenses = 0.0,

                profitPerKm = 0.0,

                profitPerHour = 0.0,

            ),

            expenseBreakdown = emptyList(),

            platformBreakdown = platformBreakdown.map { it.first to it.second.copy(

                profit = it.second.grossEarnings,

                expenses = 0.0,

                fuelCost = 0.0,

                otherExpenses = 0.0,

            ) },

            onClose = onClose,

        )

        AppAReportOverlay.Costs -> AppAMetricReport(

            title = "Custos",

            subtitle = "Combustível e gastos do período",

            summary = Summary(

                grossEarnings = 0.0,

                fuelCost = summary.fuelCost,

                otherExpenses = summary.otherExpenses,

                expenses = summary.expenses,

                profit = -summary.expenses,

                totalKm = summary.totalKm,

                profitPerKm = 0.0,

                tripCount = summary.tripCount,

                shiftCount = summary.shiftCount,

                totalMinutes = summary.totalMinutes,

                profitPerHour = 0.0,

            ),

            expenseBreakdown = expenseBreakdown,

            platformBreakdown = emptyList(),

            onClose = onClose,

        )

        AppAReportOverlay.ProfitPerKm -> AppAMetricReport(

            title = "Rendimento por km",

            subtitle = "Lucro líquido ÷ km rodados",

            summary = summary,

            expenseBreakdown = emptyList(),

            platformBreakdown = platformBreakdown,

            onClose = onClose,

            highlight = "Média: ${formatCurrency(summary.profitPerKm)}/km em ${summary.totalKm.toInt()} km",

        )

        is AppAReportOverlay.PlatformEarnings -> AppAPlatformEarningsReport(

            platform = overlay.platform,

            platformBreakdown = platformBreakdown,

            onClose = onClose,

        )

        is AppAReportOverlay.PeriodHistory -> AppAPeriodHistoryReport(

            report = overlay.report,

            onClose = onClose,

        )

    }

}



@OptIn(ExperimentalMaterial3Api::class)

@Composable

private fun AppAReportScaffold(

    title: String,

    onClose: () -> Unit,

    content: @Composable (PaddingValues) -> Unit,

) {

    Scaffold(

        containerColor = MaterialTheme.colorScheme.background,

        topBar = {

            TopAppBar(

                title = {

                    Text(

                        title,

                        fontWeight = FontWeight.Bold,

                        color = AppColors.onSurface(),

                    )

                },

                navigationIcon = {

                    IconButton(onClick = onClose) {

                        Icon(

                            Icons.AutoMirrored.Filled.ArrowBack,

                            contentDescription = "Voltar",

                            tint = AppColors.onSurface(),

                        )

                    }

                },

                colors = TopAppBarDefaults.topAppBarColors(

                    containerColor = MaterialTheme.colorScheme.surface,

                    titleContentColor = AppColors.onSurface(),

                    navigationIconContentColor = AppColors.onSurface(),

                ),

            )

        },

        content = content,

    )

}



@Composable

private fun ReportSectionTitle(text: String) {

    Text(

        text = text,

        fontWeight = FontWeight.SemiBold,

        style = MaterialTheme.typography.titleSmall,

        color = AppColors.onSurface(),

    )

}



@Composable

private fun AppAProfitOverviewReport(

    summary: Summary,

    expenseBreakdown: List<ExpenseCategoryTotal>,

    platformBreakdown: List<Pair<Platform, Summary>>,

    onClose: () -> Unit,

) {

    AppAReportScaffold(title = "Visão geral", onClose = onClose) { padding ->

        LazyColumn(

            modifier = Modifier

                .fillMaxSize()

                .padding(padding),

            contentPadding = PaddingValues(16.dp),

            verticalArrangement = Arrangement.spacedBy(14.dp),

        ) {

            item {

                DashboardContentBlock(

                    title = "Lucro líquido",

                    subtitle = "Resumo completo do período",

                ) {

                    Text(

                        formatCurrency(summary.profit),

                        style = MaterialTheme.typography.displaySmall,

                        fontWeight = FontWeight.Bold,

                        color = AppColors.primary(),

                    )

                }

            }

            item {

                SummaryGrid(summary = summary, expenseBreakdown = expenseBreakdown)

            }

            if (platformBreakdown.any { it.second.shiftCount > 0 }) {

                item {

                    ReportSectionTitle("Por aplicativo")

                }

                items(platformBreakdown.filter { it.second.shiftCount > 0 }, key = { it.first.name }) { (p, s) ->

                    PlatformSummaryRow(platform = p, summary = s)

                }

            }

        }

    }

}



@Composable

private fun AppAMetricReport(

    title: String,

    subtitle: String,

    summary: Summary,

    expenseBreakdown: List<ExpenseCategoryTotal>,

    platformBreakdown: List<Pair<Platform, Summary>>,

    onClose: () -> Unit,

    highlight: String? = null,

) {

    AppAReportScaffold(title = title, onClose = onClose) { padding ->

        LazyColumn(

            modifier = Modifier

                .fillMaxSize()

                .padding(padding),

            contentPadding = PaddingValues(16.dp),

            verticalArrangement = Arrangement.spacedBy(14.dp),

        ) {

            item {

                DashboardContentBlock(title = title, subtitle = subtitle) {

                    highlight?.let {

                        Text(

                            it,

                            color = AppColors.primary(),

                            fontWeight = FontWeight.Medium,

                            modifier = Modifier.padding(bottom = 8.dp),

                        )

                    }

                    SummaryGrid(summary = summary, expenseBreakdown = expenseBreakdown)

                }

            }

            if (platformBreakdown.isNotEmpty()) {

                item { ReportSectionTitle("Detalhe por app") }

                items(platformBreakdown.filter { it.second.shiftCount > 0 }, key = { it.first.name }) { (p, s) ->

                    PlatformSummaryRow(platform = p, summary = s)

                }

            }

        }

    }

}



@Composable

private fun AppAPlatformEarningsReport(

    platform: Platform,

    platformBreakdown: List<Pair<Platform, Summary>>,

    onClose: () -> Unit,

) {

    val summary = platformBreakdown.firstOrNull { it.first == platform }?.second

    AppAReportScaffold(title = platform.label, onClose = onClose) { padding ->

        LazyColumn(

            modifier = Modifier

                .fillMaxSize()

                .padding(padding),

            contentPadding = PaddingValues(16.dp),

            verticalArrangement = Arrangement.spacedBy(14.dp),

        ) {

            item {

                DashboardContentBlock(

                    title = "Ganhos em ${platform.label}",

                    subtitle = "Conforme jornadas salvas no período",

                ) {

                    if (summary == null || summary.shiftCount == 0) {

                        Text(

                            "Nenhuma jornada deste app no período.",

                            color = AppColors.onSurfaceVariant(),

                        )

                    } else {

                        Text(

                            formatCurrency(summary.grossEarnings),

                            style = MaterialTheme.typography.headlineMedium,

                            fontWeight = FontWeight.Bold,

                            color = AppColors.primary(),

                        )

                        Text(

                            "${summary.shiftCount} jornada(s) · ${summary.tripCount} corridas/entregas",

                            style = MaterialTheme.typography.bodySmall,

                            color = AppColors.onSurfaceVariant(),

                            modifier = Modifier.padding(top = 8.dp, bottom = 12.dp),

                        )

                        SummaryGrid(summary = summary, expenseBreakdown = emptyList())

                    }

                }

            }

        }

    }

}



@Composable

private fun AppAPeriodHistoryReport(

    report: PeriodReport,

    onClose: () -> Unit,

) {

    AppAReportScaffold(title = "Relatório — ${report.label}", onClose = onClose) { padding ->

        LazyColumn(

            modifier = Modifier

                .fillMaxSize()

                .padding(padding),

            contentPadding = PaddingValues(16.dp),

            verticalArrangement = Arrangement.spacedBy(14.dp),

        ) {

            item {

                Text(

                    "${report.dayCount} dia(s) com jornada · ${report.shifts.size} registro(s)",

                    style = MaterialTheme.typography.bodyMedium,

                    color = AppColors.onSurfaceVariant(),

                )

            }

            item {

                SummaryGrid(summary = report.summary, expenseBreakdown = report.expenseBreakdown)

            }

            if (report.platformBreakdown.isNotEmpty()) {

                item { ReportSectionTitle("Por aplicativo") }

                items(report.platformBreakdown.filter { it.second.shiftCount > 0 }, key = { it.first.name }) { (p, s) ->

                    PlatformSummaryRow(platform = p, summary = s)

                }

            }

        }

    }

}


