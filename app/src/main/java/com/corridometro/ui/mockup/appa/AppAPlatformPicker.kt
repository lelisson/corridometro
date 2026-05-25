package com.corridometro.ui.mockup.appa

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.corridometro.domain.Platform
import com.corridometro.domain.PlatformDeliveryApps
import com.corridometro.domain.PlatformRideApps
import com.corridometro.ui.components.PlatformLogo
import com.corridometro.ui.theme.AppColors

@Composable
fun AppAPlatformPicker(
    selected: Platform,
    onSelect: (Platform) -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(modifier = modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(12.dp)) {
        AppAPlatformRow(
            title = "Corrida / passageiros",
            platforms = PlatformRideApps,
            selected = selected,
            onSelect = onSelect,
        )
        AppAPlatformRow(
            title = "Entrega / delivery",
            platforms = PlatformDeliveryApps,
            selected = selected,
            onSelect = onSelect,
        )
    }
}

@Composable
private fun AppAPlatformRow(
    title: String,
    platforms: List<Platform>,
    selected: Platform,
    onSelect: (Platform) -> Unit,
) {
    Text(
        text = title,
        style = MaterialTheme.typography.labelLarge,
        fontWeight = FontWeight.SemiBold,
        color = AppColors.onSurfaceVariant(),
    )
    LazyRow(
        horizontalArrangement = Arrangement.spacedBy(10.dp),
        contentPadding = PaddingValues(vertical = 4.dp),
    ) {
        items(platforms, key = { it.name }) { platform ->
            AppAPlatformCard(
                platform = platform,
                selected = selected == platform,
                onClick = { onSelect(platform) },
            )
        }
    }
}

@Composable
private fun AppAPlatformCard(
    platform: Platform,
    selected: Boolean,
    onClick: () -> Unit,
) {
    Surface(
        modifier = Modifier
            .width(88.dp)
            .appATouch(onClick = onClick, label = platform.label),
        shape = MaterialTheme.shapes.large,
        color = MaterialTheme.colorScheme.surface,
        border = BorderStroke(
            width = if (selected) 2.dp else 1.dp,
            color = if (selected) AppColors.primary() else AppColors.outline(),
        ),
        shadowElevation = if (selected) 3.dp else 0.dp,
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            if (selected) {
                Icon(
                    Icons.Default.CheckCircle,
                    contentDescription = "Selecionado",
                    tint = AppColors.primary(),
                    modifier = Modifier
                        .align(Alignment.End)
                        .padding(bottom = 2.dp),
                )
            }
            PlatformLogo(platform = platform, size = 40.dp)
            Text(
                text = platform.label,
                style = MaterialTheme.typography.labelMedium,
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                color = if (selected) AppColors.primary() else AppColors.onSurface(),
                modifier = Modifier.padding(top = 6.dp),
                maxLines = 2,
            )
        }
    }
}
