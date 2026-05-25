package com.corridometro.ui.preview

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.corridometro.domain.PeriodFilter
import com.corridometro.ui.components.DashboardConceptAContent
import com.corridometro.ui.components.DayHistoryCard
import com.corridometro.ui.components.FormField
import com.corridometro.ui.components.JourneyLivePreviewBlock
import com.corridometro.ui.components.JourneyRegisterBlock
import com.corridometro.ui.components.JourneyTodayHistoryBlock
import com.corridometro.ui.components.PeriodSegmentedPicker
import com.corridometro.ui.components.PrimaryButton
import com.corridometro.ui.components.ProfitHeroCard
import com.corridometro.ui.components.FinanceTriangleRow
import com.corridometro.ui.theme.CorridometroTheme

private const val PreviewBg = 0xFFF2F4F7

// —— Conceito A (Início) ——

@Preview(name = "A — Início com dados", group = "Conceito A", showBackground = true, backgroundColor = PreviewBg, heightDp = 1200)
@Composable
fun PreviewConceptA_WithData() {
    CorridometroTheme {
        ConceptADashboardPreview(state = PreviewSampleData.dashboardUiState, period = PeriodFilter.MES)
    }
}

@Preview(name = "A — Início vazio", group = "Conceito A", showBackground = true, backgroundColor = PreviewBg, heightDp = 900)
@Composable
fun PreviewConceptA_Empty() {
    CorridometroTheme {
        ConceptADashboardPreview(state = PreviewSampleData.dashboardEmpty, period = PeriodFilter.HOJE)
    }
}

@Preview(name = "A — Período personalizado", group = "Conceito A", showBackground = true, backgroundColor = PreviewBg, heightDp = 1100)
@Composable
fun PreviewConceptA_CustomPeriod() {
    CorridometroTheme {
        ConceptADashboardPreview(
            state = PreviewSampleData.dashboardCustomPeriod,
            period = PeriodFilter.PERSONALIZADO,
        )
    }
}

@Preview(name = "A — Herói lucro", group = "Conceito A", showBackground = true, backgroundColor = PreviewBg, heightDp = 220)
@Composable
fun PreviewConceptA_Hero() {
    CorridometroTheme {
        ProfitHeroCard(
            profit = PreviewSampleData.sampleSummary.profit,
            periodLabel = "30 dias",
            hasData = true,
            onNavigateToJourney = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(name = "A — Herói vazio", group = "Conceito A", showBackground = true, backgroundColor = PreviewBg, heightDp = 260)
@Composable
fun PreviewConceptA_HeroEmpty() {
    CorridometroTheme {
        ProfitHeroCard(
            profit = 0.0,
            periodLabel = "Hoje",
            hasData = false,
            onNavigateToJourney = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(name = "A — Período segmentado", group = "Conceito A", showBackground = true, backgroundColor = PreviewBg, heightDp = 140)
@Composable
fun PreviewConceptA_PeriodSegments() {
    CorridometroTheme {
        PeriodSegmentedPicker(
            selected = PeriodFilter.MES,
            onSelect = {},
            onOpenCustomPeriod = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(name = "A — Triângulo financeiro", group = "Conceito A", showBackground = true, backgroundColor = PreviewBg, heightDp = 120)
@Composable
fun PreviewConceptA_Triangle() {
    CorridometroTheme {
        FinanceTriangleRow(
            summary = PreviewSampleData.sampleSummary,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(name = "A — App completo (aba Início)", group = "Conceito A", showBackground = true, backgroundColor = PreviewBg, heightDp = 1200)
@Composable
fun PreviewConceptA_AppShell() {
    CorridometroTheme {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = true,
                        onClick = {},
                        icon = { Icon(Icons.Default.Home, null) },
                        label = { Text("Início") },
                    )
                    NavigationBarItem(
                        selected = false,
                        onClick = {},
                        icon = { Icon(Icons.Default.Schedule, null) },
                        label = { Text("Jornada") },
                    )
                }
            },
        ) { padding ->
            ConceptADashboardPreview(
                state = PreviewSampleData.dashboardUiState,
                period = PeriodFilter.MES,
                modifier = Modifier.padding(padding),
            )
        }
    }
}

@Composable
private fun ConceptADashboardPreview(
    state: com.corridometro.ui.CorridometroUiState,
    period: PeriodFilter,
    modifier: Modifier = Modifier,
) {
    DashboardConceptAContent(
        summary = state.summary,
        period = period,
        customPeriodLabel = state.customPeriodLabel,
        selectedPlatforms = state.selectedPlatforms,
        platformBreakdown = state.platformBreakdown,
        expenseBreakdown = state.expenseBreakdown,
        isPremium = state.isPremium,
        subscriptionPlans = state.subscriptionPlans,
        selectedSubscriptionProductId = state.selectedSubscriptionProductId,
        isBillingReady = state.isBillingReady,
        isPurchasing = false,
        billingMessage = null,
        cloudConfigured = state.cloudConfigured,
        requireGoogleLogin = false,
        signedInEmail = state.signedInEmail,
        signedInDisplayName = state.signedInDisplayName,
        syncMessage = null,
        isSyncing = false,
        onSelectPlan = {},
        onSubscribe = {},
        onRestore = {},
        onSetPeriod = {},
        onOpenCustomPeriod = {},
        onTogglePlatform = {},
        onSignOut = {},
        onNavigateToJourney = {},
        modifier = modifier,
    )
}

// —— Conceito B (Jornada) ——

@Preview(name = "B — Preview ao vivo", group = "Conceito B", showBackground = true, backgroundColor = PreviewBg, heightDp = 420)
@Composable
fun PreviewConceptB_LivePreview() {
    CorridometroTheme {
        JourneyLivePreviewBlock(
            shift = PreviewSampleData.sampleShift,
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(name = "B — Formulário (estrutura)", group = "Conceito B", showBackground = true, backgroundColor = PreviewBg, heightDp = 520)
@Composable
fun PreviewConceptB_RegisterBlock() {
    CorridometroTheme {
        JourneyRegisterBlock(
            platform = PreviewSampleData.sampleShift.platform,
            onPlatformSelect = {},
            modifier = Modifier.padding(16.dp),
            content = {
                FormField("Km rodados", "120,5", {}, placeholder = "120,5")
                FormField("Faturamento total (R$)", "350,00", {}, placeholder = "350,00")
            },
        )
    }
}

@Preview(name = "B — Histórico hoje", group = "Conceito B", showBackground = true, backgroundColor = PreviewBg, heightDp = 280)
@Composable
fun PreviewConceptB_TodayHistory() {
    CorridometroTheme {
        JourneyTodayHistoryBlock(
            todayHistory = PreviewSampleData.dashboardUiState.dayHistory,
            hasAnyHistory = true,
            reportsCount = 3,
            onBrowseAllReports = {},
            historyRow = { item ->
                DayHistoryCard(item = item, onOpenReport = {})
            },
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(name = "B — Histórico vazio", group = "Conceito B", showBackground = true, backgroundColor = PreviewBg, heightDp = 220)
@Composable
fun PreviewConceptB_TodayEmpty() {
    CorridometroTheme {
        JourneyTodayHistoryBlock(
            todayHistory = emptyList(),
            hasAnyHistory = false,
            onBrowseAllReports = {},
            historyRow = {},
            modifier = Modifier.padding(16.dp),
        )
    }
}

@Preview(name = "B — Jornada completa", group = "Conceito B", showBackground = true, backgroundColor = PreviewBg, heightDp = 1100)
@Composable
fun PreviewConceptB_FullJourney() {
    CorridometroTheme {
        ConceptBJourneyPreview()
    }
}

@Preview(name = "B — App completo (aba Jornada)", group = "Conceito B", showBackground = true, backgroundColor = PreviewBg, heightDp = 1100)
@Composable
fun PreviewConceptB_AppShell() {
    CorridometroTheme {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                NavigationBar {
                    NavigationBarItem(
                        selected = false,
                        onClick = {},
                        icon = { Icon(Icons.Default.Home, null) },
                        label = { Text("Início") },
                    )
                    NavigationBarItem(
                        selected = true,
                        onClick = {},
                        icon = { Icon(Icons.Default.Schedule, null) },
                        label = { Text("Jornada") },
                    )
                }
            },
        ) { padding ->
            ConceptBJourneyPreview(modifier = Modifier.padding(padding))
        }
    }
}

@Preview(name = "App — A+B (Início + Jornada)", group = "Conceitos A e B", showBackground = true, backgroundColor = PreviewBg, heightDp = 1200)
@Composable
fun PreviewConceptAB_DashboardInShell() {
    PreviewConceptA_AppShell()
}

@Composable
private fun ConceptBJourneyPreview(modifier: Modifier = Modifier) {
    val state = PreviewSampleData.dashboardUiState
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp),
    ) {
        item {
            JourneyRegisterBlock(
                platform = PreviewSampleData.sampleShift.platform,
                onPlatformSelect = {},
                content = {
                    FormField("Data da jornada", "19/05/2026", {}, placeholder = "dd/mm/aaaa")
                    FormField("Km rodados", "120,5", {}, placeholder = "120,5")
                    FormField("Faturamento total (R$)", "350,00", {}, placeholder = "350,00")
                },
            )
        }
        item {
            JourneyLivePreviewBlock(shift = PreviewSampleData.sampleShift)
        }
        item {
            PrimaryButton(text = "Salvar jornada", onClick = {}, enabled = true)
        }
        item {
            JourneyTodayHistoryBlock(
                todayHistory = state.dayHistory,
                hasAnyHistory = true,
                reportsCount = state.dayHistory.size,
                onBrowseAllReports = {},
                historyRow = { item ->
                    DayHistoryCard(item = item, onOpenReport = {})
                },
            )
        }
    }
}
