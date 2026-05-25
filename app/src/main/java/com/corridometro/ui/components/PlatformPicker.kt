package com.corridometro.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.corridometro.domain.Platform
import com.corridometro.domain.PlatformMainApps
import com.corridometro.domain.isFromMoreMenu

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PlatformPicker(
    selected: Platform?,
    onSelect: (Platform?) -> Unit,
    modifier: Modifier = Modifier,
    /** Filtro na Início: sem chip "Todas"; null = todos os apps no resumo. */
    includeAll: Boolean = false,
) {
    var showMoreSheet by rememberSaveable { mutableStateOf(false) }
    val moreMenuActive = selected?.isFromMoreMenu() == true

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        if (includeAll) {
            PlatformLogoChip(
                platform = null,
                selected = selected == null,
                onClick = { onSelect(null) },
                label = "Todas",
            )
        }
        PlatformMainApps.forEach { platform ->
            PlatformLogoChip(
                platform = platform,
                selected = selected == platform,
                onClick = { onSelect(platform) },
            )
        }
        PlatformLogoChip(
            platform = if (moreMenuActive) selected else null,
            selected = moreMenuActive,
            onClick = { showMoreSheet = true },
            label = if (moreMenuActive) selected?.label ?: "Outras" else "Outras",
            showMoreBadge = !moreMenuActive,
        )
    }

    if (showMoreSheet) {
        PlatformMoreSheet(
            onDismiss = { showMoreSheet = false },
            onSelect = { platform -> onSelect(platform) },
            selected = if (moreMenuActive) selected else null,
        )
    }
}

/** Filtro na Início: multi-seleção; conjunto vazio = total geral. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardPlatformFilterPicker(
    selectedPlatforms: Set<Platform>,
    onToggle: (Platform) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showMoreSheet by rememberSaveable { mutableStateOf(false) }
    val moreSelected = selectedPlatforms.filter { it.isFromMoreMenu() }

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        PlatformMainApps.forEach { platform ->
            PlatformLogoChip(
                platform = platform,
                selected = platform in selectedPlatforms,
                onClick = { onToggle(platform) },
            )
        }
        PlatformLogoChip(
            platform = moreSelected.singleOrNull(),
            selected = moreSelected.isNotEmpty(),
            onClick = { showMoreSheet = true },
            label = when {
                moreSelected.isEmpty() -> "Outras"
                moreSelected.size == 1 -> moreSelected.first().label
                else -> "Outras (${moreSelected.size})"
            },
            showMoreBadge = moreSelected.isEmpty(),
        )
    }

    if (showMoreSheet) {
        PlatformMoreSheet(
            onDismiss = { showMoreSheet = false },
            onSelect = onToggle,
            selectedPlatforms = selectedPlatforms,
            multiSelect = true,
        )
    }
}

/** Seletor na jornada (sempre um app escolhido). */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun JourneyPlatformPicker(
    selected: Platform,
    onSelect: (Platform) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showMoreSheet by rememberSaveable { mutableStateOf(false) }
    val moreMenuActive = selected.isFromMoreMenu()

    FlowRow(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        PlatformMainApps.forEach { platform ->
            PlatformLogoChip(
                platform = platform,
                selected = selected == platform,
                onClick = { onSelect(platform) },
            )
        }
        PlatformLogoChip(
            platform = if (moreMenuActive) selected else null,
            selected = moreMenuActive,
            onClick = { showMoreSheet = true },
            label = if (moreMenuActive) selected.label else "Outras",
            showMoreBadge = !moreMenuActive,
        )
    }

    if (showMoreSheet) {
        PlatformMoreSheet(
            onDismiss = { showMoreSheet = false },
            onSelect = onSelect,
            selected = if (moreMenuActive) selected else null,
        )
    }
}
