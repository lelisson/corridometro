package com.corridometro.ui.navigation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.corridometro.R
import com.corridometro.ui.ads.PreloadInterstitialEffect
import com.corridometro.ui.components.AdBanner
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.corridometro.ui.CorridometroViewModel
import com.corridometro.ui.DesignFeatures
import com.corridometro.ui.mockup.MockupBJornadaNavHost
import com.corridometro.ui.mockup.appa.AppANavHost
import com.corridometro.ui.screens.AuthLoadingScreen
import com.corridometro.ui.screens.DashboardScreen
import com.corridometro.ui.screens.DayReportScreen
import com.corridometro.ui.screens.ShiftScreen
import com.corridometro.ui.screens.SignInScreen
import com.corridometro.ui.theme.Primary
import com.corridometro.ui.theme.TextSecondary

sealed class Screen(val route: String, val label: String, val icon: ImageVector) {
    data object Home : Screen("home", "Início", Icons.Default.Home)
    data object Shifts : Screen("shifts", "Jornada", Icons.Default.Schedule)
}

private val bottomItems = listOf(Screen.Home, Screen.Shifts)

@Composable
fun CorridometroNavHost(
    viewModel: CorridometroViewModel,
    darkTheme: Boolean = false,
    onDarkThemeChange: (Boolean) -> Unit = {},
) {
    val navController = rememberNavController()
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination
    val uiState by viewModel.uiState.collectAsState()
    val openDayReport = uiState.openDayReportEpochDay
    PreloadInterstitialEffect(isPremium = uiState.isPremium)

    LaunchedEffect(uiState.navigateToJourneySignal) {
        if (uiState.navigateToJourneySignal > 0) {
            navController.navigate(Screen.Shifts.route) {
                popUpTo(navController.graph.findStartDestination().id) {
                    saveState = true
                }
                launchSingleTop = true
                restoreState = true
            }
        }
    }

    when {
        uiState.requireGoogleLogin && !uiState.authReady -> {
            AuthLoadingScreen()
            return
        }
        uiState.requireGoogleLogin && !uiState.isAuthenticated -> {
            SignInScreen(viewModel)
            return
        }
    }

    if (DesignFeatures.isMockupInicio) {
        AppANavHost(
            viewModel = viewModel,
            darkTheme = darkTheme,
            onDarkThemeChange = onDarkThemeChange,
        )
        return
    }

    if (DesignFeatures.isMockupJornada) {
        Box(modifier = Modifier.fillMaxSize()) {
            MockupBJornadaNavHost(viewModel = viewModel)
            if (openDayReport != null) {
                BackHandler { viewModel.closeDayReport() }
            }
            openDayReport?.let { day ->
                DayReportScreen(
                    dateEpochDay = day,
                    viewModel = viewModel,
                    onClose = viewModel::closeDayReport,
                )
            }
        }
        return
    }

    Box(modifier = Modifier.fillMaxSize()) {
    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            if (!uiState.isPremium && !DesignFeatures.isMockupInicio) {
                AdBanner(adUnitId = stringResource(R.string.admob_banner_unit_id))
            }
        },
        bottomBar = {
            NavigationBar(
                containerColor = MaterialTheme.colorScheme.surface,
                tonalElevation = 0.dp,
            ) {
                bottomItems.forEach { screen ->
                    val selected = currentDestination?.hierarchy?.any { it.route == screen.route } == true
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.label) },
                        label = { Text(screen.label) },
                        selected = selected,
                        onClick = {
                            navController.navigate(screen.route) {
                                popUpTo(navController.graph.findStartDestination().id) {
                                    saveState = true
                                }
                                launchSingleTop = true
                                restoreState = true
                            }
                        },
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
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Home.route,
            modifier = Modifier.padding(innerPadding),
        ) {
            composable(Screen.Home.route) { DashboardScreen(viewModel) }
            composable(Screen.Shifts.route) { ShiftScreen(viewModel) }
        }
    }

    if (openDayReport != null) {
        BackHandler { viewModel.closeDayReport() }
    }

    openDayReport?.let { day ->
        DayReportScreen(
            dateEpochDay = day,
            viewModel = viewModel,
            onClose = viewModel::closeDayReport,
        )
    }
    }
}
