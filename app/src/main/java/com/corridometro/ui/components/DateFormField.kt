package com.corridometro.ui.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarMonth
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.corridometro.ui.theme.AppColors
import com.corridometro.util.localDateToPickerMillis
import com.corridometro.util.parseDateInput
import com.corridometro.util.pickerMillisToLocalDate
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateFormField(
    label: String,
    dateText: String,
    onDateChange: (LocalDate) -> Unit,
    modifier: Modifier = Modifier,
) {
    var showPicker by remember { mutableStateOf(false) }
    val selectedDate = remember(dateText) { parseDateInput(dateText) ?: LocalDate.now() }
    val initialMillis = remember(selectedDate) { localDateToPickerMillis(selectedDate) }

    Column(modifier = modifier.padding(bottom = 12.dp)) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelLarge,
            color = AppColors.onSurfaceVariant(),
            fontWeight = FontWeight.Medium,
            modifier = Modifier.padding(bottom = 6.dp),
        )
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { showPicker = true },
        ) {
            OutlinedTextField(
                value = dateText,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text("Toque para abrir o calendário", color = AppColors.onSurfaceVariant())
                },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    IconButton(onClick = { showPicker = true }) {
                        Icon(
                            imageVector = Icons.Default.CalendarMonth,
                            contentDescription = "Abrir calendário",
                            tint = AppColors.primary(),
                        )
                    }
                },
                colors = AppColors.formFieldColors(),
            )
        }
    }

    if (showPicker) {
        val datePickerState = rememberDatePickerState(initialSelectedDateMillis = initialMillis)

        DatePickerDialog(
            onDismissRequest = { showPicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val millis = datePickerState.selectedDateMillis ?: initialMillis
                        onDateChange(pickerMillisToLocalDate(millis))
                        showPicker = false
                    },
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showPicker = false }) {
                    Text("Cancelar")
                }
            },
        ) {
            DatePicker(state = datePickerState)
        }
    }
}
