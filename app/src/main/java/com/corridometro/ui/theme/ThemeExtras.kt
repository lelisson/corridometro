package com.corridometro.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.TextFieldColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

/** Cores derivadas do [MaterialTheme] — legíveis em claro e escuro. */
object AppColors {
    @Composable
    fun surface(): Color = MaterialTheme.colorScheme.surface

    @Composable
    fun surfaceVariant(): Color = MaterialTheme.colorScheme.surfaceVariant

    @Composable
    fun onSurface(): Color = MaterialTheme.colorScheme.onSurface

    @Composable
    fun onSurfaceVariant(): Color = MaterialTheme.colorScheme.onSurfaceVariant

    @Composable
    fun outline(): Color = MaterialTheme.colorScheme.outline

    @Composable
    fun primary(): Color = MaterialTheme.colorScheme.primary

    @Composable
    fun onPrimary(): Color = MaterialTheme.colorScheme.onPrimary

    @Composable
    fun primaryContainer(): Color = MaterialTheme.colorScheme.primaryContainer

    @Composable
    fun onPrimaryContainer(): Color = MaterialTheme.colorScheme.onPrimaryContainer

    @Composable
    fun error(): Color = MaterialTheme.colorScheme.error

    @Composable
    fun metricIconBg(tint: Color): Color = tint.copy(alpha = 0.18f)

    /** Campos de formulário (texto, data, hora) — legíveis em tema claro e escuro. */
    @Composable
    fun formFieldColors(): TextFieldColors = OutlinedTextFieldDefaults.colors(
        focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        disabledContainerColor = MaterialTheme.colorScheme.surfaceVariant,
        focusedBorderColor = MaterialTheme.colorScheme.primary,
        unfocusedBorderColor = MaterialTheme.colorScheme.outline,
        disabledBorderColor = MaterialTheme.colorScheme.outline,
        focusedTextColor = MaterialTheme.colorScheme.onSurface,
        unfocusedTextColor = MaterialTheme.colorScheme.onSurface,
        disabledTextColor = MaterialTheme.colorScheme.onSurface,
        focusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        unfocusedPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        disabledPlaceholderColor = MaterialTheme.colorScheme.onSurfaceVariant,
        cursorColor = MaterialTheme.colorScheme.primary,
    )
}
