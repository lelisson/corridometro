package com.corridometro.ui.mockup.appa



import androidx.compose.foundation.layout.Arrangement

import androidx.compose.foundation.layout.Box

import androidx.compose.foundation.layout.ExperimentalLayoutApi

import androidx.compose.foundation.layout.FlowRow

import androidx.compose.foundation.layout.PaddingValues

import androidx.compose.foundation.layout.Row

import androidx.compose.foundation.layout.fillMaxSize

import androidx.compose.foundation.layout.fillMaxWidth

import androidx.compose.foundation.layout.padding

import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.lazy.rememberLazyListState

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.CalendarMonth

import androidx.compose.material.icons.filled.CardMembership

import androidx.compose.material.icons.filled.DarkMode

import androidx.compose.material.icons.filled.Notifications

import androidx.compose.material3.Button

import androidx.compose.material3.ButtonDefaults

import androidx.compose.material3.FilterChip

import androidx.compose.material3.FilterChipDefaults

import androidx.compose.material3.Icon

import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.Switch

import androidx.compose.material3.Text

import androidx.compose.runtime.Composable

import androidx.compose.runtime.collectAsState

import androidx.compose.runtime.getValue

import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.remember

import androidx.compose.runtime.setValue

import androidx.compose.ui.Modifier

import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp

import com.corridometro.data.settings.AppSettings

import com.corridometro.domain.buildPeriodReport

import com.corridometro.domain.epochRangeForDaysBack

import com.corridometro.ui.CorridometroViewModel

import com.corridometro.ui.components.CustomPeriodDialog

import com.corridometro.ui.components.DashboardContentBlock

import com.corridometro.ui.theme.AppColors

import java.time.LocalDate



private enum class HistoryPeriod(val label: String, val days: Int?) {

    DAY("Dia", 1),

    WEEK7("7 dias", 7),

    WEEK14("14 dias", 14),

    MONTH30("30 dias", 30),

    CUSTOM("Calendário", null),

}



@OptIn(ExperimentalLayoutApi::class)

@Composable

fun AppAMenuScreen(

    viewModel: CorridometroViewModel,

    appSettings: AppSettings,

    darkTheme: Boolean,

    onDarkThemeChange: (Boolean) -> Unit,

    onOpenPeriodReport: (AppAReportOverlay.PeriodHistory) -> Unit,

    modifier: Modifier = Modifier,

) {

    val uiState by viewModel.uiState.collectAsState()

    var showSubscriptions by remember { mutableStateOf(false) }

    var historyPeriod by remember { mutableStateOf(HistoryPeriod.WEEK7) }

    var showCalendar by remember { mutableStateOf(false) }

    var customStart by remember { mutableStateOf(LocalDate.now()) }

    var customEnd by remember { mutableStateOf(LocalDate.now()) }

    val listState = rememberLazyListState()



    if (showCalendar) {

        CustomPeriodDialog(

            initialStart = customStart,

            initialEnd = customEnd,

            onDismiss = { showCalendar = false },

            onApply = { start, end ->

                customStart = start

                customEnd = end

                historyPeriod = HistoryPeriod.CUSTOM

                showCalendar = false

            },

        )

    }



    Box(modifier = modifier.fillMaxSize()) {

        LazyColumn(

            state = listState,

            modifier = Modifier.fillMaxSize(),

            contentPadding = PaddingValues(16.dp),

            verticalArrangement = Arrangement.spacedBy(14.dp),

        ) {

            item(key = "title") {

                Text(

                    "Menu",

                    style = MaterialTheme.typography.headlineSmall,

                    fontWeight = FontWeight.Bold,

                    color = AppColors.onSurface(),

                )

            }



            item(key = "subs") {
                Button(
                    onClick = { showSubscriptions = true },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = AppColors.primary(),
                        contentColor = AppColors.onPrimary(),
                    ),
                ) {
                    Text(
                        "Ver planos de assinatura",
                        fontWeight = FontWeight.SemiBold,
                        color = AppColors.onPrimary(),
                    )
                }
            }



            item(key = "settings") {

                DashboardContentBlock(

                    title = "Configurações do app",

                    subtitle = "Tema e notificações",

                ) {

                    SettingRow(

                        icon = { Icon(Icons.Default.DarkMode, null, tint = AppColors.primary()) },

                        label = "Tema escuro",

                        trailing = {

                            Switch(

                                checked = darkTheme,

                                onCheckedChange = {

                                    onDarkThemeChange(it)

                                    appSettings.darkTheme = it

                                },

                            )

                        },

                    )

                    SettingRow(

                        icon = { Icon(Icons.Default.Notifications, null, tint = AppColors.primary()) },

                        label = "Notificações",

                        trailing = {

                            Switch(

                                checked = appSettings.notificationsEnabled,

                                onCheckedChange = { appSettings.notificationsEnabled = it },

                            )

                        },

                    )

                }

            }



            item(key = "history") {

                DashboardContentBlock(

                    title = "Histórico de relatórios",

                    subtitle = "Gerado pelas jornadas salvas",

                ) {

                    FlowRow(

                        horizontalArrangement = Arrangement.spacedBy(8.dp),

                        verticalArrangement = Arrangement.spacedBy(8.dp),

                    ) {

                        HistoryPeriod.entries.forEach { option ->

                            FilterChip(

                                selected = historyPeriod == option,

                                onClick = {

                                    if (option == HistoryPeriod.CUSTOM) {

                                        showCalendar = true

                                    } else {

                                        historyPeriod = option

                                    }

                                },

                                label = {

                                    if (option == HistoryPeriod.CUSTOM) {

                                        Row {

                                            Icon(

                                                Icons.Default.CalendarMonth,

                                                null,

                                                modifier = Modifier.padding(end = 4.dp),

                                                tint = if (historyPeriod == option) {

                                                    AppColors.onPrimary()

                                                } else {

                                                    AppColors.onSurface()

                                                },

                                            )

                                            Text(

                                                option.label,

                                                color = if (historyPeriod == option) {

                                                    AppColors.onPrimary()

                                                } else {

                                                    AppColors.onSurface()

                                                },

                                            )

                                        }

                                    } else {

                                        Text(

                                            option.label,

                                            color = if (historyPeriod == option) {

                                                AppColors.onPrimary()

                                            } else {

                                                AppColors.onSurface()

                                            },

                                        )

                                    }

                                },

                                colors = FilterChipDefaults.filterChipColors(

                                    selectedContainerColor = AppColors.primary(),

                                    selectedLabelColor = AppColors.onPrimary(),

                                    containerColor = AppColors.surfaceVariant(),

                                    labelColor = AppColors.onSurface(),

                                ),

                                border = FilterChipDefaults.filterChipBorder(

                                    enabled = true,

                                    selected = historyPeriod == option,

                                    borderColor = AppColors.outline(),

                                    selectedBorderColor = AppColors.primary(),

                                ),

                            )

                        }

                    }

                    Button(

                        onClick = {

                            val (start, end) = when (historyPeriod) {

                                HistoryPeriod.CUSTOM -> customStart.toEpochDay() to customEnd.toEpochDay()

                                else -> {

                                    val days = historyPeriod.days ?: 7

                                    epochRangeForDaysBack(days)

                                }

                            }

                            val label = when (historyPeriod) {

                                HistoryPeriod.DAY -> "Hoje"

                                HistoryPeriod.WEEK7 -> "Últimos 7 dias"

                                HistoryPeriod.WEEK14 -> "Últimos 14 dias"

                                HistoryPeriod.MONTH30 -> "Últimos 30 dias"

                                HistoryPeriod.CUSTOM -> "Personalizado"

                            }

                            val report = buildPeriodReport(

                                startEpochDay = start,

                                endEpochDay = end,

                                label = label,

                                allShifts = uiState.workShifts,

                                allExpenses = uiState.expenses,

                            )

                            onOpenPeriodReport(AppAReportOverlay.PeriodHistory(report))

                        },

                        modifier = Modifier

                            .fillMaxWidth()

                            .padding(top = 12.dp),

                        colors = ButtonDefaults.buttonColors(

                            containerColor = AppColors.primary(),

                            contentColor = AppColors.onPrimary(),

                        ),

                    ) {

                        Text("Gerar relatório")

                    }

                }

            }

        }



        if (showSubscriptions) {

            AppASubscriptionsOverlay(

                viewModel = viewModel,

                onClose = { showSubscriptions = false },

            )

        }

    }

}



@Composable

private fun SettingRow(

    icon: @Composable () -> Unit,

    label: String,

    trailing: @Composable () -> Unit,

) {

    Row(

        modifier = Modifier

            .fillMaxWidth()

            .padding(vertical = 8.dp),

        verticalAlignment = androidx.compose.ui.Alignment.CenterVertically,

    ) {

        icon()

        Text(

            label,

            modifier = Modifier

                .weight(1f)

                .padding(horizontal = 12.dp),

            fontWeight = FontWeight.Medium,

            color = AppColors.onSurface(),

        )

        trailing()

    }

}


