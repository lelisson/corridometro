package com.corridometro.ui.preview

import com.corridometro.domain.Expense
import com.corridometro.domain.ExpenseCategory
import com.corridometro.domain.PeriodFilter
import com.corridometro.domain.Platform
import com.corridometro.domain.Summary
import com.corridometro.domain.WorkShift
import com.corridometro.domain.buildDayHistory
import com.corridometro.domain.calculateSummary
import com.corridometro.domain.groupExpensesByCategory
import com.corridometro.domain.toEpochDayLong
import com.corridometro.data.billing.PREMIUM_MONTHLY_ID
import com.corridometro.data.billing.PremiumPlan
import com.corridometro.ui.CorridometroUiState
import com.corridometro.ui.components.SubscriptionPlanUi
import java.time.LocalDate

object PreviewSampleData {

    private val today = LocalDate.now().toEpochDayLong()

    val sampleShift = WorkShift(
        id = 1,
        platform = Platform.UBER,
        dateEpochDay = today,
        startMinutesOfDay = 8 * 60,
        endMinutesOfDay = 18 * 60,
        km = 120.0,
        fuelKmPerLiter = 12.5,
        tripCount = 15,
        fuelPricePerLiter = 5.89,
        totalEarnings = 350.0,
    )

    val sampleExpense = Expense(
        id = 1,
        category = ExpenseCategory.ALMOCO,
        amount = 32.0,
        dateEpochDay = today,
        platform = Platform.UBER,
    )

    val sampleShifts = listOf(
        sampleShift,
        sampleShift.copy(
            id = 2,
            platform = Platform.NINETY_NINE,
            totalEarnings = 280.0,
            km = 95.0,
            tripCount = 11,
        ),
    )

    val sampleExpenses = listOf(
        sampleExpense,
        sampleExpense.copy(id = 2, category = ExpenseCategory.LANCHE, amount = 12.0),
        sampleExpense.copy(id = 3, category = ExpenseCategory.PEDAGIO, amount = 18.0),
    )

    val sampleSummary: Summary = calculateSummary(sampleShifts, sampleExpenses)

    val dashboardUiState = CorridometroUiState(
        workShifts = sampleShifts,
        expenses = sampleExpenses,
        filteredShifts = sampleShifts,
        filteredExpenses = sampleExpenses,
        summary = sampleSummary,
        platformBreakdown = Platform.entries.map { platform ->
            platform to calculateSummary(
                sampleShifts.filter { it.platform == platform },
                sampleExpenses.filter { it.platform == null || it.platform == platform },
            )
        },
        period = PeriodFilter.MES,
        expenseBreakdown = groupExpensesByCategory(sampleExpenses),
        dayHistory = buildDayHistory(sampleShifts, setOf(today)),
        finalizedDays = setOf(today),
        finalizedAtByDay = mapOf(today to System.currentTimeMillis()),
        cloudConfigured = false,
        subscriptionPlans = PremiumPlan.entries.map {
            SubscriptionPlanUi(plan = it, priceLabel = it.fallbackPrice)
        },
        selectedSubscriptionProductId = PREMIUM_MONTHLY_ID,
        isBillingReady = true,
    )

    val dashboardSignedIn = dashboardUiState.copy(
        cloudConfigured = true,
        signedInEmail = "usuario@exemplo.com",
        signedInDisplayName = "João Motorista",
    )

    val dashboardEmpty = CorridometroUiState(
        period = PeriodFilter.HOJE,
        subscriptionPlans = dashboardUiState.subscriptionPlans,
        selectedSubscriptionProductId = dashboardUiState.selectedSubscriptionProductId,
        isBillingReady = true,
    )

    val dashboardCustomPeriod = dashboardUiState.copy(
        period = PeriodFilter.PERSONALIZADO,
        customPeriodLabel = "01/05 – 19/05/2026",
    )
}
