package com.corridometro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightGrayColors = lightColorScheme(
    primary = Primary,
    onPrimary = Color.White,
    primaryContainer = PrimarySoft,
    onPrimaryContainer = Color(0xFF14532D),
    background = Background,
    onBackground = TextPrimary,
    surface = Surface,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceElevated,
    onSurfaceVariant = TextSecondary,
    outline = Border,
    error = Danger,
    onError = Color.White,
)

private val DarkColors = darkColorScheme(
    primary = Color(0xFF4ADE80),
    onPrimary = Color(0xFF052E16),
    primaryContainer = Color(0xFF14532D),
    onPrimaryContainer = Color(0xFFDCFCE7),
    background = Color(0xFF0F1419),
    onBackground = Color(0xFFF2F4F7),
    surface = Color(0xFF1A2332),
    onSurface = Color(0xFFF2F4F7),
    surfaceVariant = Color(0xFF243044),
    onSurfaceVariant = Color(0xFF98A2B3),
    outline = Color(0xFF475467),
    error = Danger,
    onError = Color.White,
)

@Composable
fun CorridometroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightGrayColors,
        content = content,
    )
}
