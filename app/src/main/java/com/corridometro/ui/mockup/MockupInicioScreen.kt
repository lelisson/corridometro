package com.corridometro.ui.mockup

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.ScaffoldDefaults
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.corridometro.domain.PeriodFilter
import com.corridometro.domain.Platform
import com.corridometro.domain.PlatformAppAAll
import com.corridometro.domain.Summary
import com.corridometro.domain.isDeliveryApp
import com.corridometro.ui.CorridometroViewModel
import com.corridometro.ui.components.CustomPeriodDialog
import com.corridometro.ui.components.PlatformLogo
import com.corridometro.ui.components.PlatformLogoChip
import com.corridometro.ui.mockup.appa.AppAReportOverlay
import com.corridometro.ui.mockup.appa.appATouch
import com.corridometro.ui.theme.AppColors
import com.corridometro.ui.theme.Danger
import com.corridometro.ui.theme.Primary
import com.corridometro.util.formatCurrency
import java.time.LocalDate

private data class MockupPeriod(val filter: PeriodFilter, val shortLabel: String)

private val mockupPeriods = listOf(
    MockupPeriod(PeriodFilter.HOJE, "Hoje"),
    MockupPeriod(PeriodFilter.SEMANA, "7d"),
    MockupPeriod(PeriodFilter.MES, "30d"),
    MockupPeriod(PeriodFilter.TUDO, "Tudo"),
)

@Composable
fun MockupInicioScreen(
    viewModel: CorridometroViewModel,
    onOpenReport: (AppAReportOverlay) -> Unit,
    modifier: Modifier = Modifier,
) {
    val uiState by viewModel.uiState.collectAsState()
    val period by viewModel.period.collectAsState()
    var showCustomPeriod by remember { mutableStateOf(false) }
    val listState = rememberLazyListState()

    if (showCustomPeriod) {
        val today = LocalDate.now()
        val start = uiState.customRangeStart?.let { LocalDate.ofEpochDay(it) } ?: today
        val end = uiState.customRangeEnd?.let { LocalDate.ofEpochDay(it) } ?: today
        CustomPeriodDialog(
            initialStart = start,
            initialEnd = end,
            onDismiss = { showCustomPeriod = false },
            onApply = { s, e ->
                viewModel.applyCustomDateRange(s, e)
                showCustomPeriod = false
            },
        )
    }

    val periodSubtitle = when (period) {
        PeriodFilter.HOJE -> "Hoje"
        PeriodFilter.SEMANA -> "7 dias"
        PeriodFilter.MES -> "30 dias"
        PeriodFilter.TUDO -> "Tudo"
        PeriodFilter.PERSONALIZADO -> uiState.customPeriodLabel ?: "Período"
    }

    val summary = uiState.summary
    val hasData = summary.shiftCount > 0
    val activePlatforms = uiState.platformBreakdown.filter { it.second.shiftCount > 0 }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = MaterialTheme.colorScheme.background,
        contentWindowInsets = ScaffoldDefaults.contentWindowInsets.only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Top,
        ),
        topBar = { MockupInicioTopBar() },
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = viewModel::navigateToJourneyForm,
                containerColor = Primary,
                contentColor = Color.White,
            ) {
                Icon(Icons.Default.Add, null)
                Text("Nova jornada", fontWeight = FontWeight.Bold, modifier = Modifier.padding(start = 8.dp))
            }
        },
    ) { padding ->
        LazyColumn(
            state = listState,
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(bottom = 0.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            item(key = "hero") {
                MockupProfitHeroCard(
                    profit = summary.profit,
                    periodSubtitle = periodSubtitle,
                    hasData = hasData,
                    onTap = { onOpenReport(AppAReportOverlay.ProfitOverview) },
                    modifier = Modifier.padding(start = 16.dp, end = 16.dp, top = 16.dp),
                )
            }

            item(key = "period") {
                MockupPeriodSegmentedRow(
                    selected = period,
                    onSelect = viewModel::setPeriod,
                    onCustomPeriod = { showCustomPeriod = true },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }

            item(key = "filter") {
                Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                    Text(
                        "Filtrar por app",
                        style = MaterialTheme.typography.labelLarge,
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.onSurface(),
                    )
                    Text(
                        if (uiState.selectedPlatforms.isEmpty()) {
                            "Nenhum selecionado — somando todos os apps"
                        } else if (uiState.selectedPlatforms.size == 1) {
                            "Exibindo só ${uiState.selectedPlatforms.first().label}"
                        } else {
                            "Somando ${uiState.selectedPlatforms.size} apps selecionados"
                        },
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.onSurfaceVariant(),
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
                    )
                    LazyRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                    ) {
                        items(PlatformAppAAll, key = { it.name }) { platform ->
                            PlatformLogoChip(
                                platform = platform,
                                selected = platform in uiState.selectedPlatforms,
                                onClick = { viewModel.togglePlatformFilter(platform) },
                                label = platform.label,
                            )
                        }
                    }
                }
            }

            item(key = "metrics") {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                ) {
                    MockupMetricTile(
                        icon = Icons.Default.AttachMoney,
                        iconTint = AppColors.primary(),
                        iconBg = AppColors.primaryContainer(),
                        label = "Faturamento",
                        value = formatCurrency(summary.grossEarnings),
                        trend = if (hasData) "+10%" else "—",
                        trendPositive = true,
                        onClick = { onOpenReport(AppAReportOverlay.Revenue) },
                        modifier = Modifier.weight(1f),
                    )
                    MockupMetricTile(
                        icon = Icons.Default.AccountBalanceWallet,
                        iconTint = Danger,
                        iconBg = AppColors.metricIconBg(Danger),
                        label = "Custos",
                        value = formatCurrency(summary.expenses),
                        trend = if (hasData) "+6%" else "—",
                        trendPositive = false,
                        onClick = { onOpenReport(AppAReportOverlay.Costs) },
                        modifier = Modifier.weight(1f),
                    )
                    MockupMetricTile(
                        icon = Icons.Default.Speed,
                        iconTint = AppColors.primary(),
                        iconBg = AppColors.primaryContainer(),
                        label = "R\$/km",
                        value = formatCurrency(summary.profitPerKm),
                        trend = if (hasData) "+8%" else "—",
                        trendPositive = true,
                        onClick = { onOpenReport(AppAReportOverlay.ProfitPerKm) },
                        modifier = Modifier.weight(1f),
                    )
                }
            }

            item(key = "platforms") {
                MockupPorAplicativoSection(
                    breakdown = activePlatforms,
                    totalTrips = summary.tripCount,
                    onPlatformClick = { onOpenReport(AppAReportOverlay.PlatformEarnings(it)) },
                    modifier = Modifier.padding(horizontal = 16.dp),
                )
            }
        }
    }
}

@Composable
private fun MockupInicioTopBar() {
    Surface(
        color = AppColors.surface(),
        shadowElevation = 2.dp,
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 14.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Icon(Icons.Outlined.Speed, null, tint = AppColors.primary(), modifier = Modifier.size(28.dp))
            Text(
                text = "Corridômetro",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = AppColors.primary(),
                modifier = Modifier.padding(start = 10.dp),
            )
        }
    }
}

@Composable
private fun MockupProfitHeroCard(
    profit: Double,
    periodSubtitle: String,
    hasData: Boolean,
    onTap: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .appATouch(onClick = onTap, label = "Relatório visão geral"),
        shape = MaterialTheme.shapes.extraLarge,
        color = AppColors.surface(),
        shadowElevation = 3.dp,
        border = BorderStroke(1.dp, AppColors.outline()),
    ) {
        Row(
            modifier = Modifier.padding(20.dp),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    "Lucro líquido · $periodSubtitle",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.onSurfaceVariant(),
                )
                Text(
                    formatCurrency(profit),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = AppColors.onSurface(),
                    modifier = Modifier.padding(vertical = 6.dp),
                )
                Text(
                    if (hasData) "Toque para visão geral do período" else "Toque para começar",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.primary(),
                    fontWeight = FontWeight.Medium,
                )
            }
            Box(
                modifier = Modifier
                    .size(72.dp)
                    .clip(CircleShape)
                    .background(AppColors.primaryContainer()),
                contentAlignment = Alignment.Center,
            ) {
                Icon(Icons.AutoMirrored.Filled.TrendingUp, null, tint = AppColors.primary(), modifier = Modifier.size(36.dp))
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
private fun MockupPeriodSegmentedRow(
    selected: PeriodFilter,
    onSelect: (PeriodFilter) -> Unit,
    onCustomPeriod: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier) {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            mockupPeriods.forEachIndexed { index, item ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = mockupPeriods.size),
                    onClick = { onSelect(item.filter) },
                    selected = selected == item.filter,
                    label = { Text(item.shortLabel) },
                )
            }
        }
        TextButton(onClick = onCustomPeriod, modifier = Modifier.align(Alignment.End)) {
            Text(
                if (selected == PeriodFilter.PERSONALIZADO) "Período personalizado ativo" else "Período personalizado…",
                color = AppColors.primary(),
                fontSize = 12.sp,
            )
        }
    }
}

@Composable
private fun MockupMetricTile(
    icon: ImageVector,
    iconTint: Color,
    iconBg: Color,
    label: String,
    value: String,
    trend: String,
    trendPositive: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.appATouch(onClick = onClick, label = "Relatório $label"),
        shape = MaterialTheme.shapes.large,
        color = AppColors.surface(),
        border = BorderStroke(1.dp, AppColors.outline()),
        shadowElevation = 1.dp,
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(iconBg),
                contentAlignment = Alignment.Center,
            ) {
                Icon(icon, null, tint = iconTint, modifier = Modifier.size(20.dp))
            }
            Text(
                label,
                style = MaterialTheme.typography.labelSmall,
                color = AppColors.onSurfaceVariant(),
                modifier = Modifier.padding(top = 8.dp),
            )
            Text(
                value,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = AppColors.onSurface(),
                modifier = Modifier.padding(top = 2.dp),
            )
            if (trend != "—") {
                Text(
                    "$trend vs ontem",
                    style = MaterialTheme.typography.labelSmall,
                    color = if (trendPositive) AppColors.primary() else Danger,
                    modifier = Modifier.padding(top = 4.dp),
                )
            }
        }
    }
}

@Composable
private fun MockupPorAplicativoSection(
    breakdown: List<Pair<Platform, Summary>>,
    totalTrips: Int,
    onPlatformClick: (Platform) -> Unit,
    modifier: Modifier = Modifier,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = AppColors.surface(),
        border = BorderStroke(1.dp, AppColors.outline()),
        shadowElevation = 2.dp,
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                "Por aplicativo",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.titleMedium,
                color = AppColors.onSurface(),
            )
            if (breakdown.isEmpty()) {
                Text(
                    "Registre jornadas para ver o resumo por app.",
                    style = MaterialTheme.typography.bodySmall,
                    color = AppColors.onSurfaceVariant(),
                    modifier = Modifier.padding(top = 12.dp),
                )
            } else {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 12.dp, bottom = 8.dp),
                ) {
                    Box(modifier = Modifier.weight(1.2f))
                    Text(
                        "Faturamento",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.onSurfaceVariant(),
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        "Custos",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.onSurfaceVariant(),
                        modifier = Modifier.weight(1f),
                    )
                    Text(
                        "R\$/km",
                        style = MaterialTheme.typography.labelSmall,
                        color = AppColors.onSurfaceVariant(),
                        modifier = Modifier.weight(0.8f),
                    )
                }
                breakdown.forEach { (platform, platformSummary) ->
                    val pct = if (totalTrips > 0) platformSummary.tripCount * 100 / totalTrips else 0
                    val tipo = if (platform.isDeliveryApp()) "entregas" else "corridas"
                    MockupPlatformBreakdownRow(
                        platform = platform,
                        tripPercent = pct,
                        tripLabel = tipo,
                        summary = platformSummary,
                        onClick = { onPlatformClick(platform) },
                    )
                }
            }
        }
    }
}

@Composable
private fun MockupPlatformBreakdownRow(
    platform: Platform,
    tripPercent: Int,
    tripLabel: String,
    summary: Summary,
    onClick: () -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .appATouch(onClick = onClick, label = "Resumo ${platform.label}")
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        PlatformLogo(platform = platform, size = 40.dp)
        Column(modifier = Modifier.weight(1.2f).padding(start = 10.dp)) {
            Text(platform.label, fontWeight = FontWeight.SemiBold, color = AppColors.onSurface())
            Text(
                "$tripPercent% das $tripLabel",
                style = MaterialTheme.typography.bodySmall,
                color = AppColors.onSurfaceVariant(),
            )
        }
        Text(
            formatCurrency(summary.grossEarnings),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = AppColors.onSurface(),
            modifier = Modifier.weight(1f),
        )
        Text(
            formatCurrency(summary.expenses),
            style = MaterialTheme.typography.bodySmall,
            color = Danger,
            modifier = Modifier.weight(1f),
        )
        Text(
            formatCurrency(summary.profitPerKm),
            style = MaterialTheme.typography.bodySmall,
            color = AppColors.primary(),
            fontWeight = FontWeight.SemiBold,
            modifier = Modifier.weight(0.8f),
        )
        Icon(Icons.Default.ChevronRight, null, tint = AppColors.onSurfaceVariant(), modifier = Modifier.size(20.dp))
    }
}
