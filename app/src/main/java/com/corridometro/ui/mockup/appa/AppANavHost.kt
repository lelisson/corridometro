package com.corridometro.ui.mockup.appa

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.corridometro.R
import com.corridometro.ui.ads.PreloadInterstitialEffect
import com.corridometro.ui.ads.runWithOptionalInterstitial
import com.corridometro.ui.components.AdBanner
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.corridometro.CorridometroApp
import com.corridometro.ui.CorridometroViewModel
import com.corridometro.ui.mockup.MockupInicioScreen
import com.corridometro.ui.screens.DayReportScreen
import com.corridometro.ui.theme.AppColors

private sealed class AppATab(val route: String, val label: String, val icon: ImageVector) {
    data object Inicio : AppATab("inicio", "Início", Icons.Default.Home)
    data object Menu : AppATab("menu", "Menu", Icons.Default.Menu)
}

private val tabs = listOf(AppATab.Inicio, AppATab.Menu)

@Composable
fun AppANavHost(
    viewModel: CorridometroViewModel,
    darkTheme: Boolean,
    onDarkThemeChange: (Boolean) -> Unit,
) {
    val context = LocalContext.current
    val navController = rememberNavController()
    val navBackStack by navController.currentBackStackEntryAsState()
    val uiState by viewModel.uiState.collectAsState()
    val app = LocalContext.current.applicationContext as CorridometroApp
    var reportOverlay by remember { mutableStateOf<AppAReportOverlay?>(null) }
    var showJourneyForm by rememberSaveable { mutableStateOf(false) }

    PreloadInterstitialEffect(isPremium = uiState.isPremium)

    val openReport: (AppAReportOverlay) -> Unit = { overlay ->
        runWithOptionalInterstitial(context, uiState.isPremium) {
            reportOverlay = overlay
        }
    }

    LaunchedEffect(uiState.openJourneyFormSignal) {
        if (uiState.openJourneyFormSignal > 0) {
            showJourneyForm = true
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background,
            topBar = {
                if (!uiState.isPremium) {
                    AdBanner(adUnitId = stringResource(R.string.admob_banner_unit_id))
                }
            },
            bottomBar = {
                NavigationBar(
                    containerColor = MaterialTheme.colorScheme.surface,
                    tonalElevation = 0.dp,
                ) {
                    tabs.forEach { tab ->
                        val selected = navBackStack?.destination?.route == tab.route
                        NavigationBarItem(
                            selected = selected,
                            onClick = {
                                navController.navigate(tab.route) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            icon = { Icon(tab.icon, tab.label) },
                            label = { Text(tab.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AppColors.primary(),
                                selectedTextColor = AppColors.primary(),
                                indicatorColor = MaterialTheme.colorScheme.primaryContainer,
                                unselectedIconColor = AppColors.onSurfaceVariant(),
                                unselectedTextColor = AppColors.onSurfaceVariant(),
                            ),
                        )
                    }
                }
            },
        ) { padding ->
            NavHost(
                navController = navController,
                startDestination = AppATab.Inicio.route,
                modifier = Modifier.padding(padding),
            ) {
                composable(AppATab.Inicio.route) {
                    MockupInicioScreen(
                        viewModel = viewModel,
                        onOpenReport = openReport,
                    )
                }
                composable(AppATab.Menu.route) {
                    AppAMenuScreen(
                        viewModel = viewModel,
                        appSettings = app.appSettings,
                        darkTheme = darkTheme,
                        onDarkThemeChange = onDarkThemeChange,
                        onOpenPeriodReport = openReport,
                    )
                }
            }
        }

        if (showJourneyForm) {
            BackHandler { showJourneyForm = false }
            AppAJourneyFormOverlay(
                viewModel = viewModel,
                onClose = { showJourneyForm = false },
            )
        }

        AppAReportHost(
            overlay = reportOverlay,
            summary = uiState.summary,
            expenseBreakdown = uiState.expenseBreakdown,
            platformBreakdown = uiState.platformBreakdown,
            onClose = { reportOverlay = null },
        )

        uiState.openDayReportEpochDay?.let { day ->
            BackHandler { viewModel.closeDayReport() }
            DayReportScreen(
                dateEpochDay = day,
                viewModel = viewModel,
                onClose = viewModel::closeDayReport,
            )
        }
    }
}
