package com.corridometro.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.corridometro.domain.DayUpdateMode
import com.corridometro.ui.DuplicateDayPrompt
import com.corridometro.ui.theme.Surface as SurfaceColor
import com.corridometro.util.formatDate

@Composable
fun DuplicateDayDialog(
    prompt: DuplicateDayPrompt,
    onDismiss: () -> Unit,
    onConfirm: (DayUpdateMode) -> Unit,
) {
    val dateLabel = formatDate(prompt.incoming.dateEpochDay)
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = SurfaceColor,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text(
                    "Já existe jornada em ${prompt.incoming.platform.label}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    "No dia $dateLabel você já registrou ${prompt.incoming.platform.label}. " +
                        "Outros apps do mesmo dia não serão alterados.",
                    style = MaterialTheme.typography.bodyMedium,
                )
                Text(
                    "• Adicionar: outro registro do mesmo app (valores separados no relatório).\n" +
                        "• Somar: junta tudo deste app em um só registro.\n" +
                        "• Substituir: troca o último registro deste app.",
                    style = MaterialTheme.typography.bodySmall,
                )
                PrimaryButton(
                    text = "Adicionar registro",
                    onClick = {
                        onConfirm(DayUpdateMode.ADD)
                        onDismiss()
                    },
                )
                SecondaryButton(
                    text = "Somar neste app",
                    onClick = {
                        onConfirm(DayUpdateMode.SUM)
                        onDismiss()
                    },
                )
                SecondaryButton(
                    text = "Substituir este app",
                    onClick = {
                        onConfirm(DayUpdateMode.REPLACE)
                        onDismiss()
                    },
                )
                TextButton(
                    onClick = onDismiss,
                    modifier = Modifier.fillMaxWidth(),
                ) {
                    Text("Cancelar")
                }
            }
        }
    }
}
