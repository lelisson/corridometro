package com.corridometro.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.corridometro.ui.theme.AppColors

/**
 * Bloco Material 3 — agrupa conteúdo relacionado (índice visual na tela).
 */
@Composable
fun DashboardContentBlock(
    title: String,
    modifier: Modifier = Modifier,
    subtitle: String? = null,
    content: @Composable ColumnScope.() -> Unit,
) {
    Surface(
        modifier = modifier.fillMaxWidth(),
        shape = MaterialTheme.shapes.large,
        color = AppColors.surface(),
        tonalElevation = 1.dp,
        shadowElevation = 2.dp,
        border = BorderStroke(1.dp, AppColors.outline()),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            content = {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = AppColors.onSurface(),
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = AppColors.onSurfaceVariant(),
                        modifier = Modifier.padding(top = 4.dp, bottom = 8.dp),
                    )
                } else {
                    Spacer(modifier = Modifier.height(8.dp))
                }
                content()
            },
        )
    }
}
