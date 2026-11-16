package com.example.sleephelper.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Alarm
import androidx.compose.material.icons.filled.Bed
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sleephelper.ui.theme.AppColors
import com.example.sleephelper.ui.theme.AppRadius
import com.example.sleephelper.utils.*
import com.example.sleephelper.widgets.PrimaryButton
import com.example.sleephelper.widgets.SleepTimePickerDialog
import java.time.LocalTime

private val badges = mapOf(
    4 to "Мінімум",
    5 to "Оптимально",
    6 to "Повноцінно"
)

@Composable
fun CalcScreen() {
    var isWake by remember { mutableStateOf(true) }
    var wakeTime by remember { mutableStateOf(makeTime(7, 0)) }
    var bedTime by remember { mutableStateOf(LocalTime.now()) }
    var results by remember { mutableStateOf<List<CycleResult>?>(null) }
    var showPicker by remember { mutableStateOf(false) }

    val activeTime = if (isWake) wakeTime else bedTime

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "Калькулятор сну",
            color = AppColors.text,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Розрахунок $CYCLE_MIN-хвилинних циклів",
            color = AppColors.muted,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(20.dp))

        ModeSegment(
            isWake = isWake,
            onChanged = { newMode ->
                isWake = newMode
                results = null
            }
        )

        Spacer(modifier = Modifier.height(28.dp))

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(AppRadius.md))
                .clickable { showPicker = true }
                .padding(vertical = 8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = formatTime(activeTime),
                color = AppColors.text,
                fontSize = 76.sp,
                fontWeight = FontWeight.ExtraBold,
                letterSpacing = 1.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isWake) Icons.Default.Alarm else Icons.Default.Bed,
                    contentDescription = null,
                    modifier = Modifier.size(14.dp),
                    tint = AppColors.accent
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = if (isWake) "Встановлено час підйому" else "Встановлено час відбою",
                    color = AppColors.accent,
                    fontSize = 13.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Торкніться часу, щоб змінити",
                color = AppColors.muted,
                fontSize = 11.sp
            )
        }

        PrimaryButton(
            label = "Розрахувати",
            modifier = Modifier.padding(top = 24.dp),
            onPressed = {
                results = if (isWake) bedtimesForWake(wakeTime) else wakeTimesForBed(bedTime)
            }
        )

        results?.let { list ->
            Spacer(modifier = Modifier.height(24.dp))
            Text(
                text = if (isWake) "РЕКОМЕНДОВАНО ЛЯГТИ:" else "РЕКОМЕНДОВАНО ПРОКИНУТИСЬ:",
                color = AppColors.muted,
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(10.dp))
            list.forEach { r ->
                ResultCard(result = r)
            }
            Text(
                text = "Враховано $FALL_ASLEEP_MIN хв на засинання. Прокидатися наприкінці циклу легше, ніж посеред нього.",
                color = AppColors.muted,
                fontSize = 12.sp,
                lineHeight = 17.sp,
                modifier = Modifier.padding(top = 6.dp)
            )
        }
    }

    if (showPicker) {
        SleepTimePickerDialog(
            initialTime = activeTime,
            onDismiss = { showPicker = false },
            onConfirm = { picked ->
                if (isWake) wakeTime = picked else bedTime = picked
                results = null
                showPicker = false
            }
        )
    }
}

@Composable
private fun ModeSegment(
    isWake: Boolean,
    onChanged: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppRadius.md))
            .background(AppColors.card)
            .border(1.dp, AppColors.border, RoundedCornerShape(AppRadius.md))
            .padding(4.dp)
    ) {
        listOf("Хочу прокинутись" to true, "Хочу лягти" to false).forEach { (label, value) ->
            val active = isWake == value
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedCornerShape(AppRadius.sm))
                    .background(if (active) AppColors.input else Color.Transparent)
                    .clickable { onChanged(value) }
                    .padding(vertical = 11.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    color = if (active) AppColors.text else AppColors.muted,
                    fontSize = 13.sp,
                    fontWeight = if (active) FontWeight.ExtraBold else FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ResultCard(result: CycleResult) {
    val optimal = result.cycles == OPTIMAL_CYCLES

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 10.dp)
            .clip(RoundedCornerShape(AppRadius.md))
            .background(AppColors.card)
            .border(
                width = 1.dp,
                color = if (optimal) AppColors.accent else AppColors.border,
                shape = RoundedCornerShape(AppRadius.md)
            )
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = formatTime(result.time),
                color = AppColors.text,
                fontSize = 24.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${result.cycles} ${cyclesWord(result.cycles)} (${formatDuration(result.sleepMin)} сну)",
                color = AppColors.muted,
                fontSize = 12.sp
            )
        }

        Box(
            modifier = Modifier
                .clip(CircleShape)
                .background(if (optimal) Color(0x2EFBBF24) else AppColors.input)
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = badges[result.cycles] ?: "",
                color = if (optimal) AppColors.accent else AppColors.muted,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
