package com.corridometro.ui.preview



import androidx.compose.foundation.layout.padding

import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Surface

import androidx.compose.runtime.Composable

import androidx.compose.ui.Modifier

import androidx.compose.ui.tooling.preview.Preview

import androidx.compose.ui.unit.dp

import com.corridometro.domain.ExpenseCategory

import com.corridometro.domain.Platform

import com.corridometro.domain.buildDayReport

import com.corridometro.ui.components.DayHistoryCard

import com.corridometro.ui.components.DayReportContent

import com.corridometro.ui.components.JourneyExpenseFields

import com.corridometro.ui.components.JourneyPlatformPicker

import com.corridometro.ui.components.SubscriptionPlansSection

import com.corridometro.ui.screens.ShiftPreviewCard

import com.corridometro.ui.screens.ShiftRow

import com.corridometro.ui.theme.CorridometroTheme



private const val PreviewBg = 0xFFF2F4F7



/** @see ConceptPreviews — cenários A (Início) e B (Jornada) com grupos no painel Preview. */



@Preview(name = "Planos de assinatura", showBackground = true, backgroundColor = PreviewBg, heightDp = 520)

@Composable

fun PreviewSubscriptionPlans() {

    CorridometroTheme {

        SubscriptionPlansSection(

            isPremium = false,

            plans = PreviewSampleData.dashboardUiState.subscriptionPlans,

            selectedProductId = PreviewSampleData.dashboardUiState.selectedSubscriptionProductId,

            isBillingReady = true,

            isPurchasing = false,

            billingMessage = null,

            onSelectPlan = {},

            onSubscribe = {},

            onRestore = {},

            modifier = Modifier.padding(16.dp),

        )

    }

}



@Preview(name = "Inicio — com dados (legado)", showBackground = true, backgroundColor = PreviewBg, heightDp = 1200)

@Composable

fun PreviewDashboardWithData() {

    PreviewConceptA_WithData()

}



@Preview(name = "Apps — seletor", showBackground = true, backgroundColor = PreviewBg, heightDp = 200)

@Composable

fun PreviewPlatformPicker() {

    CorridometroTheme {

        androidx.compose.foundation.layout.Column(Modifier.padding(16.dp)) {

            JourneyPlatformPicker(

                selected = Platform.UBER,

                onSelect = {},

            )

        }

    }

}



@Preview(name = "Jornada — resumo (legado)", showBackground = true, backgroundColor = PreviewBg)

@Composable

fun PreviewShiftCalculation() {

    CorridometroTheme {

        ShiftPreviewCard(PreviewSampleData.sampleShift)

    }

}



@Preview(name = "Jornada — historico linha", showBackground = true, backgroundColor = PreviewBg)

@Composable

fun PreviewShiftHistoryRow() {

    CorridometroTheme {

        ShiftRow(PreviewSampleData.sampleShift)

    }

}



@Preview(name = "Jornada — dia no historico", showBackground = true, backgroundColor = PreviewBg)

@Composable

fun PreviewDayHistoryCard() {

    CorridometroTheme {

        DayHistoryCard(

            item = PreviewSampleData.dashboardUiState.dayHistory.first(),

            onOpenReport = {},

            modifier = Modifier.padding(16.dp),

        )

    }

}



@Preview(name = "Relatorio do dia", showBackground = true, backgroundColor = PreviewBg, heightDp = 900)

@Composable

fun PreviewDayReport() {

    val state = PreviewSampleData.dashboardUiState

    val today = PreviewSampleData.sampleShift.dateEpochDay

    val report = buildDayReport(

        today,

        state.workShifts,

        state.expenses,

        state.finalizedAtByDay[today],

    )

    CorridometroTheme {

        DayReportContent(report = report)

    }

}



@OptIn(androidx.compose.foundation.ExperimentalFoundationApi::class)
@Preview(name = "Jornada — gastos em cascata", showBackground = true, backgroundColor = PreviewBg, heightDp = 520)
@Composable
fun PreviewJourneyExpenses() {

    CorridometroTheme {

        Surface(

            modifier = Modifier.padding(16.dp),

            shape = MaterialTheme.shapes.large,

        ) {

            JourneyExpenseFields(

                amounts = mapOf(

                    ExpenseCategory.ALMOCO to "32,00",

                    ExpenseCategory.PEDAGIO to "18,00",

                ),

                onAmountChange = { _, _ -> },

                modifier = Modifier.padding(16.dp),

            )

        }

    }

}



@Preview(name = "App — 2 abas (legado → A)", showBackground = true, backgroundColor = PreviewBg, heightDp = 1200)

@Composable

fun PreviewAppShell() {

    PreviewConceptA_AppShell()

}


