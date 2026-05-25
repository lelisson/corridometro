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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.corridometro.ui.theme.Surface as SurfaceColor
import com.corridometro.util.formatDateInput
import java.time.LocalDate

@Composable
fun CustomPeriodDialog(
    initialStart: LocalDate,
    initialEnd: LocalDate,
    onDismiss: () -> Unit,
    onApply: (LocalDate, LocalDate) -> Unit,
) {
    var startText by remember { mutableStateOf(formatDateInput(initialStart)) }
    var endText by remember { mutableStateOf(formatDateInput(initialEnd)) }

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
                    text = "Período personalizado",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                )
                Text(
                    text = "Escolha a data inicial e final para filtrar o resumo.",
                    style = MaterialTheme.typography.bodySmall,
                )
                DateFormField(
                    label = "Data inicial",
                    dateText = startText,
                    onDateChange = { startText = formatDateInput(it) },
                )
                DateFormField(
                    label = "Data final",
                    dateText = endText,
                    onDateChange = { endText = formatDateInput(it) },
                )
                PrimaryButton(
                    text = "Aplicar período",
                    onClick = {
                        val start = com.corridometro.util.parseDateInput(startText)
                        val end = com.corridometro.util.parseDateInput(endText)
                        if (start != null && end != null) {
                            onApply(start, end)
                            onDismiss()
                        }
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
