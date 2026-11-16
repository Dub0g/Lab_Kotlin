package com.example.sleephelper.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

object AppColors {
    val bg = Color(0xFF0B1020)
    val card = Color(0xFF1B2333)
    val sheet = Color(0xFF1F2937)
    val input = Color(0xFF111827)
    val border = Color(0xFF2A3447)
    val accent = Color(0xFFFBBF24)
    val accentText = Color(0xFF1A1305)
    val text = Color(0xFFFFFFFF)
    val muted = Color(0xFF8B95A7)
    val success = Color(0xFF34D399)
    val danger = Color(0xFFF87171)
}

object AppRadius {
    val sm = 10.dp
    val md = 14.dp
    val lg = 20.dp
    val xl = 28.dp
}

private val DarkColorScheme = darkColorScheme(
    primary = AppColors.accent,
    onPrimary = AppColors.accentText,
    surface = AppColors.sheet,
    onSurface = AppColors.text,
    background = AppColors.bg,
    onBackground = AppColors.text,
    error = AppColors.danger
)

@Composable
fun SleepHelperTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        content = content
    )
}
