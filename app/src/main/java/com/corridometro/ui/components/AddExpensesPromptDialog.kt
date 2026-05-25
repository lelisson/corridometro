package com.corridometro.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import com.corridometro.ui.theme.Surface as SurfaceColor
import com.corridometro.util.formatDate

@Composable
fun AddExpensesPromptDialog(
    dateEpochDay: Long,
    onAddExpenses: () -> Unit,
    onSkip: () -> Unit,
) {
    Dialog(onDismissRequest = onSkip) {
        Surface(
            shape = MaterialTheme.shapes.large,
            color = SurfaceColor,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp),
            ) {
                Text("Jornada salva", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text(
                    "Deseja adicionar gastos do dia ${formatDate(dateEpochDay)} agora?",
                    style = MaterialTheme.typography.bodyMedium,
                )
                PrimaryButton(text = "Sim, adicionar gastos", onClick = onAddExpenses)
                SecondaryButton(text = "Agora não", onClick = onSkip)
            }
        }
    }
}
