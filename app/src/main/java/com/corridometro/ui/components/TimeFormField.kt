package com.corridometro.ui.components

import android.widget.TimePicker
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.corridometro.ui.theme.AppColors
import com.corridometro.util.formatTime
import com.corridometro.util.parseTime

@Composable
fun TimeFormField(
    label: String,
    timeText: String,
    onTimeChange: (String) -> Unit,
    modifier: Modifier = Modifier,
    defaultHour: Int = 8,
    defaultMinute: Int = 0,
) {
    var showPicker by remember { mutableStateOf(false) }
    val parsed = remember(timeText) { parseTime(timeText) }
    val hour = parsed?.div(60) ?: defaultHour
    val minute = parsed?.rem(60) ?: defaultMinute

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
                value = timeText,
                onValueChange = {},
                readOnly = true,
                enabled = false,
                modifier = Modifier.fillMaxWidth(),
                placeholder = { Text("Toque para escolher", color = AppColors.onSurfaceVariant()) },
                singleLine = true,
                shape = RoundedCornerShape(12.dp),
                trailingIcon = {
                    IconButton(onClick = { showPicker = true }) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = "Escolher horário",
                            tint = AppColors.primary(),
                        )
                    }
                },
                colors = AppColors.formFieldColors(),
            )
        }
    }

    if (showPicker) {
        var selectedHour by remember(showPicker) { mutableIntStateOf(hour) }
        var selectedMinute by remember(showPicker) { mutableIntStateOf(minute) }

        AlertDialog(
            onDismissRequest = { showPicker = false },
            title = { Text(label) },
            text = {
                AndroidView(
                    modifier = Modifier.fillMaxWidth(),
                    factory = { context ->
                        TimePicker(context).apply {
                            setIs24HourView(true)
                            this.hour = selectedHour
                            this.minute = selectedMinute
                            setOnTimeChangedListener { _, h, m ->
                                selectedHour = h
                                selectedMinute = m
                            }
                        }
                    },
                    update = { picker ->
                        if (picker.hour != selectedHour) picker.hour = selectedHour
                        if (picker.minute != selectedMinute) picker.minute = selectedMinute
                    },
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        onTimeChange(formatTime(selectedHour * 60 + selectedMinute))
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
        )
    }
}
