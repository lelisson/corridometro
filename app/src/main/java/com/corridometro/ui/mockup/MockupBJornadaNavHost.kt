package com.corridometro.ui.mockup

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Summarize
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import com.corridometro.ui.CorridometroViewModel
import com.corridometro.ui.screens.DayReportsBrowserScreen
import com.corridometro.ui.screens.ShiftScreen
import com.corridometro.ui.theme.Primary
import com.corridometro.ui.theme.TextSecondary

private enum class MockupTab(val label: String, val icon: ImageVector) {
    Resumo("Resumo", Icons.Default.Summarize),
    Jornada("Jornada", Icons.Default.Schedule),
    Relatorios("Relatórios", Icons.Default.BarChart),
    Perfil("Perfil", Icons.Default.Person),
}

/** Navegação do APK B — 4 abas como no mockup da Jornada. */
@Composable
fun MockupBJornadaNavHost(viewModel: CorridometroViewModel) {
    var selectedTab by remember { mutableStateOf(MockupTab.Jornada) }
    var showReportsBrowser by remember { mutableStateOf(false) }
    val uiState by viewModel.uiState.collectAsState()

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            bottomBar = {
                NavigationBar(containerColor = MaterialTheme.colorScheme.surface) {
                    MockupTab.entries.forEach { tab ->
                        NavigationBarItem(
                            selected = selectedTab == tab,
                            onClick = {
                                selectedTab = tab
                                if (tab == MockupTab.Relatorios) {
                                    showReportsBrowser = true
                                }
                            },
                            icon = { Icon(tab.icon, tab.label) },
                            label = { Text(tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = Primary,
                                selectedTextColor = Primary,
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = TextSecondary,
                                unselectedTextColor = TextSecondary,
                            ),
                        )
                    }
                }
            },
        ) { padding ->
            when (selectedTab) {
                MockupTab.Resumo -> MockupInicioScreen(
                    viewModel = viewModel,
                    onOpenReport = {},
                    modifier = Modifier.padding(padding),
                )
                MockupTab.Jornada -> Box(Modifier.padding(padding)) {
                    ShiftScreen(viewModel = viewModel)
                }
                MockupTab.Relatorios -> {
                    if (uiState.dayHistory.isEmpty()) {
                        Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                            Text("Nenhum relatório ainda.", color = TextSecondary)
                        }
                    } else {
                        Box(Modifier.padding(padding)) {
                            DayReportsBrowserScreen(
                                days = uiState.dayHistory,
                                onOpenReport = viewModel::openDayReport,
                                onClose = { selectedTab = MockupTab.Jornada },
                            )
                        }
                    }
                }
                MockupTab.Perfil -> {
                    Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                        Text("Perfil — em breve", color = TextSecondary)
                    }
                }
            }
        }

        if (showReportsBrowser && selectedTab != MockupTab.Relatorios) {
            DayReportsBrowserScreen(
                days = uiState.dayHistory,
                onOpenReport = {
                    viewModel.openDayReport(it)
                    showReportsBrowser = false
                },
                onClose = { showReportsBrowser = false },
            )
        }
    }
}
