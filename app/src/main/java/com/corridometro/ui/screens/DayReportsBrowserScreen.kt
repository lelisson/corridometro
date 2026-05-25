package com.corridometro.ui.screens

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
import androidx.compose.ui.unit.dp
import com.corridometro.domain.DayHistoryItem
import com.corridometro.ui.components.DayHistoryCard
import com.corridometro.ui.theme.AppColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DayReportsBrowserScreen(
    days: List<DayHistoryItem>,
    onOpenReport: (Long) -> Unit,
    onClose: () -> Unit,
) {
    BackHandler(onBack = onClose)

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TopAppBar(
                title = {
                    Text("Consultar relatórios", color = AppColors.onSurface())
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
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding),
            contentPadding = PaddingValues(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
        ) {
            item {
                Text(
                    text = "Todos os dias com jornada salva. Toque para abrir o relatório completo (ganhos, gastos e apps).",
                    style = MaterialTheme.typography.bodyMedium,
                    color = AppColors.onSurfaceVariant(),
                )
            }
            if (days.isEmpty()) {
                item {
                    Text(
                        text = "Nenhum relatório disponível ainda.",
                        style = MaterialTheme.typography.bodyMedium,
                        color = AppColors.onSurfaceVariant(),
                    )
                }
            } else {
                items(days, key = { it.dateEpochDay }) { day ->
                    DayHistoryCard(
                        item = day,
                        onOpenReport = { onOpenReport(day.dateEpochDay) },
                        showPlatforms = true,
                    )
                }
            }
        }
    }
}
