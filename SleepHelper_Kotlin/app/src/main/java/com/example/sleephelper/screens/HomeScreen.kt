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
import androidx.compose.material.icons.filled.Calculate
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.MusicNote
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sleephelper.state.SleepViewModel
import com.example.sleephelper.ui.theme.AppColors
import com.example.sleephelper.ui.theme.AppRadius
import com.example.sleephelper.utils.formatDuration
import java.time.LocalTime
import kotlin.math.roundToInt

private const val USER_NAME = "Богдан"

private fun greeting(): String {
    val h = LocalTime.now().hour
    return when (h) {
        in 5..11 -> "Доброго ранку,"
        in 12..17 -> "Добрий день,"
        in 18..22 -> "Добрий вечір,"
        else -> "Доброї ночі,"
    }
}

private data class QuickTile(
    val title: String,
    val icon: ImageVector,
    val color: Color,
    val tab: Int
)

private val tiles = listOf(
    QuickTile("Щоденник", Icons.Default.MenuBook, Color(0xFF818CF8), 1),
    QuickTile("Розрахунок", Icons.Default.Calculate, AppColors.accent, 3),
    QuickTile("Дихання", Icons.Default.WaterDrop, Color(0xFF38BDF8), 4),
    QuickTile("Звуки", Icons.Default.MusicNote, AppColors.success, 4)
)

@Composable
fun HomeScreen(
    viewModel: SleepViewModel,
    onNavigate: (Int) -> Unit
) {
    val recent = viewModel.entries.take(7)
    val hasData = recent.isNotEmpty()
    val avgQuality = if (hasData) recent.sumOf { it.quality }.toDouble() / recent.size else 0.0
    val percent = (((avgQuality + 1.0) / 5.0) * 100.0).roundToInt()
    val avgMin = if (hasData) (recent.sumOf { it.durationMin }.toDouble() / recent.size).roundToInt() else 0

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .verticalScroll(rememberScrollState())
            .padding(start = 20.dp, top = 12.dp, end = 20.dp, bottom = 24.dp),
        horizontalAlignment = Alignment.Start
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(greeting(), color = AppColors.muted, fontSize = 13.sp)
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = USER_NAME,
                    color = AppColors.text,
                    fontSize = 24.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .clip(CircleShape)
                    .background(AppColors.accent),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = USER_NAME.first().toString(),
                    color = AppColors.accentText,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold
                )
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

        QualityCard(
            hasData = hasData,
            percent = percent,
            count = recent.size,
            avgMin = avgMin
        )

        Spacer(modifier = Modifier.height(26.dp))

        Text(
            text = "Швидкий доступ",
            color = AppColors.text,
            fontSize = 17.sp,
            fontWeight = FontWeight.Bold
        )

        Spacer(modifier = Modifier.height(12.dp))

        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            tiles.chunked(2).forEach { rowItems ->
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    rowItems.forEach { tile ->
                        TileCard(
                            tile = tile,
                            modifier = Modifier.weight(1f),
                            onTap = { onNavigate(tile.tab) }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun QualityCard(
    hasData: Boolean,
    percent: Int,
    count: Int,
    avgMin: Int
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppRadius.lg))
            .background(AppColors.card)
            .border(1.dp, AppColors.border, RoundedCornerShape(AppRadius.lg))
            .padding(18.dp)
    ) {
        Text("Якість сну", color = AppColors.muted, fontSize = 13.sp)
        Spacer(modifier = Modifier.height(6.dp))

        if (hasData) {
            Text(
                text = buildAnnotatedString {
                    withStyle(
                        SpanStyle(
                            color = AppColors.text,
                            fontSize = 40.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    ) {
                        append("$percent")
                    }
                    withStyle(
                        SpanStyle(
                            color = AppColors.accent,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.ExtraBold
                        )
                    ) {
                        append("%")
                    }
                }
            )
            Spacer(modifier = Modifier.height(10.dp))
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp))
                    .background(AppColors.input)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxHeight()
                        .fillMaxWidth((percent / 100f).coerceIn(0f, 1f))
                        .background(AppColors.accent)
                )
            }
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Середня оцінка за останні записи ($count). Середня тривалість сну: ${formatDuration(avgMin)}.",
                color = AppColors.muted,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        } else {
            Text(
                text = "—",
                color = AppColors.text,
                fontSize = 40.sp,
                fontWeight = FontWeight.ExtraBold
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = "Додайте перший запис у щоденнику, і тут з’явиться ваша статистика.",
                color = AppColors.muted,
                fontSize = 12.sp,
                lineHeight = 17.sp
            )
        }
    }
}

@Composable
private fun TileCard(
    tile: QuickTile,
    modifier: Modifier = Modifier,
    onTap: () -> Unit
) {
    Column(
        modifier = modifier
            .height(96.dp)
            .clip(RoundedCornerShape(AppRadius.md))
            .background(AppColors.card)
            .border(1.dp, AppColors.border, RoundedCornerShape(AppRadius.md))
            .clickable(onClick = onTap)
            .padding(14.dp),
        verticalArrangement = Arrangement.SpaceBetween,
        horizontalAlignment = Alignment.Start
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(RoundedCornerShape(10.dp))
                .background(tile.color.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = tile.icon,
                contentDescription = tile.title,
                tint = tile.color,
                modifier = Modifier.size(20.dp)
            )
        }
        Text(
            text = tile.title,
            color = AppColors.text,
            fontSize = 13.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
