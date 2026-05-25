package com.corridometro.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.corridometro.domain.ExpenseCategoryTotal
import com.corridometro.domain.PeriodFilter
import com.corridometro.domain.Platform
import com.corridometro.domain.Summary
import com.corridometro.ui.theme.Primary
import com.corridometro.ui.theme.TextSecondary

/** Layout clássico da aba Início (antes do Conceito A). Usado no APK «Corridômetro B». */
@Composable
fun DashboardLegacyContent(
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
    onSelectPlan: (String) -> Unit,
    onSubscribe: () -> Unit,
    onRestore: () -> Unit,
    onSetPeriod: (PeriodFilter) -> Unit,
    onOpenCustomPeriod: () -> Unit,
    onTogglePlatform: (Platform) -> Unit,
    onSignOut: () -> Unit,
    modifier: Modifier = Modifier,
    googleAccountSlot: @Composable () -> Unit = {},
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            DashboardContentBlock(
                title = "Início",
                subtitle = "Lucro real na palma da mão",
            ) {
                customPeriodLabel?.let { label ->
                    Text(
                        text = "Período ativo: $label",
                        style = MaterialTheme.typography.bodySmall,
                        color = Primary,
                    )
                }
                if (requireGoogleLogin && signedInEmail != null) {
                    val greeting = signedInDisplayName ?: signedInEmail
                    Text(
                        text = "Olá, $greeting",
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextSecondary,
                        modifier = Modifier.padding(top = 8.dp),
                    )
                    TextButton(onClick = onSignOut) {
                        Text("Trocar conta Google")
                    }
                }
            }
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

        if (cloudConfigured) {
            item { googleAccountSlot() }
        }

        item {
            DashboardContentBlock(
                title = "Período",
                subtitle = "Filtre o resumo financeiro",
            ) {
                PeriodPicker(
                    selected = period,
                    onSelect = onSetPeriod,
                    onOpenCalendar = onOpenCustomPeriod,
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
            DashboardContentBlock(
                title = "Resumo do período",
                subtitle = "Receita, custos e desempenho",
            ) {
                SummaryGrid(
                    summary = summary,
                    expenseBreakdown = expenseBreakdown,
                )
                if (summary.shiftCount == 0 && expenseBreakdown.isEmpty()) {
                    Text(
                        text = "Registre sua jornada na aba Jornada (ganhos e gastos no mesmo lugar).",
                        color = TextSecondary,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(top = 12.dp),
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
    }
}
