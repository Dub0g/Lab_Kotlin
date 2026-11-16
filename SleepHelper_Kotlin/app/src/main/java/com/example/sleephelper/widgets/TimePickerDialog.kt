package com.example.sleephelper.widgets

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.font.FontWeight
import com.example.sleephelper.ui.theme.AppColors
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SleepTimePickerDialog(
    initialTime: LocalTime,
    onDismiss: () -> Unit,
    onConfirm: (LocalTime) -> Unit
) {
    val state = rememberTimePickerState(
        initialHour = initialTime.hour,
        initialMinute = initialTime.minute,
        is24Hour = true
    )

    AlertDialog(
        onDismissRequest = onDismiss,
        containerColor = AppColors.sheet,
        titleContentColor = AppColors.text,
        text = {
            TimePicker(
                state = state,
                colors = TimePickerDefaults.colors(
                    clockDialColor = AppColors.input,
                    clockDialSelectedContentColor = AppColors.accentText,
                    clockDialUnselectedContentColor = AppColors.text,
                    selectorColor = AppColors.accent,
                    containerColor = AppColors.sheet,
                    timeSelectorSelectedContainerColor = AppColors.card,
                    timeSelectorUnselectedContainerColor = AppColors.input,
                    timeSelectorSelectedContentColor = AppColors.accent,
                    timeSelectorUnselectedContentColor = AppColors.text
                )
            )
        },
        confirmButton = {
            TextButton(onClick = { onConfirm(LocalTime.of(state.hour, state.minute)) }) {
                Text("ОК", color = AppColors.accent, fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Скасувати", color = AppColors.muted)
            }
        }
    )
}
