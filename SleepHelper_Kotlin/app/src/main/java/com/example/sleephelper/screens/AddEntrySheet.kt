package com.example.sleephelper.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sleephelper.Quality
import com.example.sleephelper.models.SleepEntry
import com.example.sleephelper.qualities
import com.example.sleephelper.state.SleepViewModel
import com.example.sleephelper.ui.theme.AppColors
import com.example.sleephelper.ui.theme.AppRadius
import com.example.sleephelper.utils.*
import com.example.sleephelper.widgets.PrimaryButton
import com.example.sleephelper.widgets.SleepTimePickerDialog
import java.time.LocalTime

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEntrySheet(
    viewModel: SleepViewModel,
    onDismiss: () -> Unit,
    onSavedWithError: (String?) -> Unit
) {
    var bedtime by remember { mutableStateOf(makeTime(23, 30)) }
    var wake by remember { mutableStateOf(makeTime(7, 15)) }
    var quality by remember { mutableIntStateOf(3) }
    var notes by remember { mutableStateOf("") }

    var pickingBed by remember { mutableStateOf(false) }
    var pickingWake by remember { mutableStateOf(false) }

    val durationMin = calcDurationMinutes(bedtime, wake)
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = AppColors.sheet,
        scrimColor = Color(0xB3050812),
        shape = RoundedCornerShape(topStart = AppRadius.xl, topEnd = AppRadius.xl),
        dragHandle = {
            Box(
                modifier = Modifier
                    .padding(top = 10.dp, bottom = 14.dp)
                    .size(width = 40.dp, height = 4.dp)
                    .clip(RoundedCornerShape(2.dp))
                    .background(AppColors.border)
            )
        }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp)
                .padding(bottom = 28.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Додати запис 🌙",
                color = AppColors.text,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 18.dp)
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                TimeBox(
                    label = "Ліг спати",
                    value = bedtime,
                    modifier = Modifier.weight(1f),
                    onTap = { pickingBed = true }
                )
                TimeBox(
                    label = "Прокинувся",
                    value = wake,
                    modifier = Modifier.weight(1f),
                    onTap = { pickingWake = true }
                )
            }

            Text(
                text = "Тривалість: ${formatDuration(durationMin)}",
                color = AppColors.accent,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 16.dp)
            )

            SheetLabel("Якість сну")
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(AppRadius.lg))
                    .background(AppColors.input)
                    .padding(6.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                qualities.forEachIndexed { i, q ->
                    QualityItem(
                        quality = q,
                        active = i == quality,
                        onTap = { quality = i }
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = qualities[quality].label,
                color = AppColors.muted,
                fontSize = 12.sp
            )

            Spacer(modifier = Modifier.height(14.dp))
            SheetLabel("Нотатки (необов’язково)")
            OutlinedTextField(
                value = notes,
                onValueChange = { if (it.length <= 200) notes = it },
                placeholder = {
                    Text(
                        "Швидко заснув, але прокидався вночі...",
                        color = AppColors.muted,
                        fontSize = 14.sp
                    )
                },
                minLines = 3,
                maxLines = 6,
                textStyle = TextStyle(color = AppColors.text, fontSize = 14.sp),
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(AppRadius.md),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = AppColors.input,
                    unfocusedContainerColor = AppColors.input,
                    focusedBorderColor = AppColors.accent,
                    unfocusedBorderColor = AppColors.border,
                    cursorColor = AppColors.accent
                )
            )

            PrimaryButton(
                label = "Зберегти",
                loading = viewModel.saving,
                modifier = Modifier.padding(top = 18.dp),
                onPressed = {
                    val entry = SleepEntry(
                        date = todayKey(),
                        bedtime = formatTime(bedtime),
                        wake = formatTime(wake),
                        durationMin = durationMin,
                        quality = quality,
                        notes = notes.trim()
                    )
                    viewModel.addEntry(entry) { err ->
                        viewModel.clearError()
                        onSavedWithError(err)
                    }
                }
            )
        }
    }

    if (pickingBed) {
        SleepTimePickerDialog(
            initialTime = bedtime,
            onDismiss = { pickingBed = false },
            onConfirm = {
                bedtime = it
                pickingBed = false
            }
        )
    }

    if (pickingWake) {
        SleepTimePickerDialog(
            initialTime = wake,
            onDismiss = { pickingWake = false },
            onConfirm = {
                wake = it
                pickingWake = false
            }
        )
    }
}

@Composable
private fun SheetLabel(text: String) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 6.dp),
        contentAlignment = Alignment.CenterStart
    ) {
        Text(text = text, color = AppColors.muted, fontSize = 12.sp)
    }
}

@Composable
private fun TimeBox(
    label: String,
    value: LocalTime,
    modifier: Modifier = Modifier,
    onTap: () -> Unit
) {
    Column(modifier = modifier) {
        SheetLabel(label)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppRadius.md))
                .background(AppColors.input)
                .border(1.dp, AppColors.border, RoundedCornerShape(AppRadius.md))
                .clickable(onClick = onTap)
                .padding(vertical = 14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = formatTime(value),
                color = AppColors.text,
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
private fun QualityItem(
    quality: Quality,
    active: Boolean,
    onTap: () -> Unit
) {
    Box(
        modifier = Modifier
            .size(width = 52.dp, height = 44.dp)
            .clip(RoundedCornerShape(AppRadius.md))
            .background(if (active) AppColors.card else Color.Transparent)
            .then(
                if (active) Modifier.border(1.dp, AppColors.accent, RoundedCornerShape(AppRadius.md))
                else Modifier
            )
            .clickable(onClick = onTap),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = quality.emoji,
            fontSize = if (active) 28.sp else 22.sp,
            modifier = Modifier.alpha(if (active) 1f else 0.55f)
        )
    }
}
