package com.corridometro.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.corridometro.domain.ExpenseCategoryTotal
import com.corridometro.domain.PeriodFilter
import com.corridometro.domain.Platform
import com.corridometro.domain.Summary
import com.corridometro.ui.theme.Border
import com.corridometro.ui.theme.Danger
import com.corridometro.ui.theme.DangerSoft
import com.corridometro.ui.theme.Primary
import com.corridometro.ui.theme.PrimarySoft
import com.corridometro.ui.theme.Surface as SurfaceColor
import com.corridometro.ui.theme.TextPrimary
import com.corridometro.ui.theme.TextSecondary
import com.corridometro.util.formatCurrency

private val segmentedPeriods = listOf(
    PeriodFilter.HOJE,
    PeriodFilter.SEMANA,
    PeriodFilter.MES,
    PeriodFilter.TUDO,
)

fun periodDisplayLabel(period: PeriodFilter, customPeriodLabel: String?): String =
    when {
        period == PeriodFilter.PERSONALIZADO && customPeriodLabel != null -> customPeriodLabel
        period == PeriodFilter.PERSONALIZADO -> PeriodFilter.PERSONALIZADO.label
        else -> period.label
    }

/** Conceito A — Início: herói de lucro, período em segmentos, triângulo financeiro, detalhes expansíveis. */
@Composable
fun DashboardConceptAContent(
    summary: Summary,
    period: PeriodFilter,
    customPeriodLabel: String?,
    selectedPlatforms: Set<Platform>,
    platformBreakdown: List<Pair<Platform, Summary>>,
    expenseBreakdown: List<ExpenseCategoryTotal>,
    isPremium: Boolean,
    subscriptionPlans: List<SubscriptionPlanUi>,
    selectedSubscriptionProductId: String?,
    isBillingReady: Boolean,
    isPurchasing: Boolean,
    billingMessage: String?,
    cloudConfigured: Boolean,
    requireGoogleLogin: Boolean,
    signedInEmail: String?,
    signedInDisplayName: String?,
    syncMessage: String?,
    isSyncing: Boolean,
    onSelectPlan: (String) -> Unit,
    onSubscribe: () -> Unit,
    onRestore: () -> Unit,
    onSetPeriod: (PeriodFilter) -> Unit,
    onOpenCustomPeriod: () -> Unit,
    onTogglePlatform: (Platform) -> Unit,
    onSignOut: () -> Unit,
    onNavigateToJourney: () -> Unit,
    modifier: Modifier = Modifier,
    googleAccountSlot: @Composable () -> Unit = {},
) {
    var detailsExpanded by remember { mutableStateOf(false) }
    val periodLabel = periodDisplayLabel(period, customPeriodLabel)
    val hasData = summary.shiftCount > 0 || summary.expenses > 0

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            ProfitHeroCard(
                profit = summary.profit,
                periodLabel = periodLabel,
                hasData = hasData,
                onNavigateToJourney = onNavigateToJourney,
            )
        }

        item {
            DashboardContentBlock(
                title = "Período",
                subtitle = "Filtre o resumo financeiro",
            ) {
                PeriodSegmentedPicker(
                    selected = period,
                    onSelect = onSetPeriod,
                    onOpenCustomPeriod = onOpenCustomPeriod,
                )
            }
        }

        item {
            DashboardContentBlock(
                title = "Filtrar por app",
                subtitle = if (selectedPlatforms.isEmpty()) {
                    "Total geral — toque nos ícones para filtrar"
                } else {
                    "Somando: ${selectedPlatforms.joinToString { it.label }}"
                },
            ) {
                DashboardPlatformFilterPicker(
                    selectedPlatforms = selectedPlatforms,
                    onToggle = onTogglePlatform,
                )
            }
        }

        item {
            FinanceTriangleRow(summary = summary)
        }

        item {
            DashboardContentBlock(
                title = "Detalhes do período",
                subtitle = if (detailsExpanded) "Receita, custos e desempenho" else "Toque para expandir",
            ) {
                TextButton(
                    onClick = { detailsExpanded = !detailsExpanded },
                    modifier = Modifier.padding(bottom = if (detailsExpanded) 4.dp else 0.dp),
                ) {
                    Text(
                        if (detailsExpanded) "Ocultar detalhes" else "Ver detalhes completos",
                        color = Primary,
                        fontWeight = FontWeight.SemiBold,
                    )
                }
                if (detailsExpanded) {
                    SummaryGrid(
                        summary = summary,
                        expenseBreakdown = expenseBreakdown,
                    )
                }
            }
        }

        if (platformBreakdown.any { it.second.shiftCount > 0 }) {
            item {
                DashboardContentBlock(
                    title = "Faturamento bruto por aplicativo",
                    subtitle = "Somente ganhos por app — custos e lucro estão no resumo",
                ) {
                    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        platformBreakdown
                            .filter { it.second.shiftCount > 0 }
                            .forEach { (platform, platformSummary) ->
                                PlatformGrossEarningsRow(
                                    platform = platform,
                                    grossEarnings = platformSummary.grossEarnings,
                                    shiftCount = platformSummary.shiftCount,
                                    tripCount = platformSummary.tripCount,
                                )
                            }
                    }
                }
            }
        }

        if (!hasData) {
            item {
                Text(
                    text = "Registre sua jornada na aba Jornada (ganhos e gastos no mesmo lugar).",
                    color = TextSecondary,
                    style = MaterialTheme.typography.bodyMedium,
                )
            }
        }

        if (requireGoogleLogin && signedInEmail != null) {
            item {
                DashboardContentBlock(
                    title = "Conta",
                    subtitle = signedInDisplayName ?: signedInEmail,
                ) {
                    TextButton(onClick = onSignOut) {
                        Text("Trocar conta Google")
                    }
                }
            }
        }

        if (cloudConfigured && !requireGoogleLogin) {
            item { googleAccountSlot() }
        }

        if (!isPremium) {
            item {
                SubscriptionPlansSection(
                    isPremium = false,
                    plans = subscriptionPlans,
                    selectedProductId = selectedSubscriptionProductId.orEmpty(),
                    isBillingReady = isBillingReady,
                    isPurchasing = isPurchasing,
                    billingMessage = billingMessage,
                    onSelectPlan = onSelectPlan,
                    onSubscribe = onSubscribe,
                    onRestore = onRestore,
                )
            }
        }
    }
}

@Composable
fun ProfitHeroCard(
    profit: Double,
    periodLabel: String,
    hasData: Boolean,
    onNavigateToJourney: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val positive = profit >= 0
    val containerColor = if (positive) PrimarySoft else DangerSoft
    val valueColor = if (positive) Primary else Danger

    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.extraLarge,
        color = containerColor,
        tonalElevation = 2.dp,
        border = BorderStroke(1.dp, if (positive) Primary.copy(alpha = 0.25f) else Danger.copy(alpha = 0.25f)),
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            Text(
                text = "Lucro líquido",
                style = MaterialTheme.typography.labelLarge,
                color = TextSecondary,
            )
            Text(
                text = formatCurrency(profit),
                style = MaterialTheme.typography.displaySmall,
                fontWeight = FontWeight.Bold,
                color = valueColor,
                modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
            )
            Text(
                text = periodLabel,
                style = MaterialTheme.typography.bodyMedium,
                color = TextSecondary,
            )
            if (!hasData) {
                Text(
                    text = "Nenhuma jornada neste período",
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    modifier = Modifier.padding(top = 8.dp),
                )
                TextButton(
                    onClick = onNavigateToJourney,
                    modifier = Modifier.padding(top = 4.dp),
                ) {
                    Text("Registrar na Jornada", color = Primary, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

@OptIn(androidx.compose.material3.ExperimentalMaterial3Api::class)
@Composable
fun PeriodSegmentedPicker(
    selected: PeriodFilter,
    onSelect: (PeriodFilter) -> Unit,
    onOpenCustomPeriod: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(10.dp)) {
        SingleChoiceSegmentedButtonRow(modifier = Modifier.fillMaxWidth()) {
            segmentedPeriods.forEachIndexed { index, period ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = segmentedPeriods.size),
                    onClick = { onSelect(period) },
                    selected = selected == period,
                    label = { Text(period.label, maxLines = 1) },
                )
            }
        }
        FilterChip(
            selected = selected == PeriodFilter.PERSONALIZADO,
            onClick = onOpenCustomPeriod,
            label = { Text(PeriodFilter.PERSONALIZADO.label) },
            colors = FilterChipDefaults.filterChipColors(
                selectedContainerColor = Primary,
                selectedLabelColor = Color.White,
                containerColor = SurfaceColor,
                labelColor = TextPrimary,
            ),
            border = FilterChipDefaults.filterChipBorder(
                enabled = true,
                selected = selected == PeriodFilter.PERSONALIZADO,
                borderColor = Border,
                selectedBorderColor = Primary,
            ),
        )
    }
}

@Composable
fun FinanceTriangleRow(
    summary: Summary,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        StatCard(
            label = "Faturamento",
            value = formatCurrency(summary.grossEarnings),
            modifier = Modifier.weight(1f),
            valueColor = Primary,
        )
        StatCard(
            label = "Custos",
            value = formatCurrency(summary.expenses),
            modifier = Modifier.weight(1f),
            valueColor = Danger,
        )
        StatCard(
            label = "R$/km",
            value = formatCurrency(summary.profitPerKm),
            modifier = Modifier.weight(1f),
        )
    }
}
