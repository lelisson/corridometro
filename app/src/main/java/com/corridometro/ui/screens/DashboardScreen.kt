package com.corridometro.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.corridometro.ui.CorridometroViewModel
import com.corridometro.ui.DesignFeatures
import com.corridometro.ui.mockup.MockupInicioScreen

@Composable
fun DashboardScreen(viewModel: CorridometroViewModel) {
    if (DesignFeatures.isMockupInicio) {
        MockupInicioScreen(viewModel = viewModel, onOpenReport = {})
        return
    }

    LegacyDashboardScreen(viewModel = viewModel)
}

/** Início clássico / intermediário (APK B na aba Resumo usa [MockupInicioScreen]). */
@Composable
fun LegacyDashboardScreen(viewModel: CorridometroViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    val period by viewModel.period.collectAsState()
    var showCustomPeriod by remember { mutableStateOf(false) }

    if (showCustomPeriod) {
        val today = java.time.LocalDate.now()
        val start = uiState.customRangeStart?.let { java.time.LocalDate.ofEpochDay(it) } ?: today
        val end = uiState.customRangeEnd?.let { java.time.LocalDate.ofEpochDay(it) } ?: today
        com.corridometro.ui.components.CustomPeriodDialog(
            initialStart = start,
            initialEnd = end,
            onDismiss = { showCustomPeriod = false },
            onApply = { startDate, endDate ->
                viewModel.applyCustomDateRange(startDate, endDate)
                showCustomPeriod = false
            },
        )
    }

    val googleSlot: @Composable () -> Unit = {
        if (uiState.cloudConfigured && !uiState.requireGoogleLogin) {
            com.corridometro.ui.components.GoogleAccountCard(
                viewModel = viewModel,
                cloudConfigured = true,
                signedInEmail = uiState.signedInEmail,
                syncMessage = uiState.syncMessage,
                isSyncing = uiState.isSyncing,
            )
        }
    }

    com.corridometro.ui.components.DashboardLegacyContent(
        summary = uiState.summary,
        period = period,
        customPeriodLabel = uiState.customPeriodLabel,
        selectedPlatforms = uiState.selectedPlatforms,
        platformBreakdown = uiState.platformBreakdown,
        expenseBreakdown = uiState.expenseBreakdown,
        isPremium = uiState.isPremium,
        subscriptionPlans = uiState.subscriptionPlans,
        selectedSubscriptionProductId = uiState.selectedSubscriptionProductId,
        isBillingReady = uiState.isBillingReady,
        isPurchasing = uiState.isPurchasing,
        billingMessage = uiState.billingMessage,
        cloudConfigured = uiState.cloudConfigured,
        requireGoogleLogin = uiState.requireGoogleLogin,
        signedInEmail = uiState.signedInEmail,
        signedInDisplayName = uiState.signedInDisplayName,
        onSelectPlan = viewModel::selectSubscriptionPlan,
        onSubscribe = viewModel::purchasePremium,
        onRestore = viewModel::restorePremium,
        onSetPeriod = viewModel::setPeriod,
        onOpenCustomPeriod = { showCustomPeriod = true },
        onTogglePlatform = viewModel::togglePlatformFilter,
        onSignOut = viewModel::signOut,
        googleAccountSlot = googleSlot,
    )
}
