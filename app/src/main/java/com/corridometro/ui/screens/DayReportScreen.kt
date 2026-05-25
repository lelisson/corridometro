package com.corridometro.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import com.corridometro.domain.buildDayReport
import com.corridometro.ui.CorridometroViewModel
import com.corridometro.ui.components.DayReportContent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayReportScreen(
    dateEpochDay: Long,
    viewModel: CorridometroViewModel,
    onClose: () -> Unit,
) {
    val uiState by viewModel.uiState.collectAsState()
    BackHandler(onBack = onClose)

    val report = remember(
        dateEpochDay,
        uiState.workShifts,
        uiState.expenses,
        uiState.finalizedAtByDay,
    ) {
        buildDayReport(
            dateEpochDay,
            uiState.workShifts,
            uiState.expenses,
            uiState.finalizedAtByDay[dateEpochDay],
        )
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        "Relatório do dia",
                        color = com.corridometro.ui.theme.AppColors.onSurface(),
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onClose) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Voltar")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface,
                ),
            )
        },
    ) { padding ->
        DayReportContent(
            report = report,
            modifier = Modifier.padding(padding),
            showShiftDetails = true,
            onEditShift = viewModel::startEditShift,
            onDeleteShift = viewModel::deleteWorkShift,
        )
    }
}
