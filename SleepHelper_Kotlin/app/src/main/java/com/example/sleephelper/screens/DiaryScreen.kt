package com.example.sleephelper.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material3.Icon
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.sleephelper.models.SleepEntry
import com.example.sleephelper.qualities
import com.example.sleephelper.state.SleepViewModel
import com.example.sleephelper.ui.theme.AppColors
import com.example.sleephelper.ui.theme.AppRadius
import com.example.sleephelper.utils.formatDuration
import com.example.sleephelper.widgets.PrimaryButton
import kotlinx.coroutines.launch

@Composable
fun DiaryScreen(
    viewModel: SleepViewModel,
    snackbarHostState: SnackbarHostState
) {
    val entries = viewModel.entries
    var showAddSheet by remember { mutableStateOf(false) }
    val scope = rememberCoroutineScope()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .statusBarsPadding()
            .padding(start = 20.dp, top = 12.dp, end = 20.dp)
    ) {
        Text(
            text = "Щоденник сну",
            color = AppColors.text,
            fontSize = 28.sp,
            fontWeight = FontWeight.ExtraBold
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Ваші записи про сон",
            color = AppColors.muted,
            fontSize = 13.sp
        )
        Spacer(modifier = Modifier.height(16.dp))

        Box(modifier = Modifier.weight(1f)) {
            if (entries.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 40.dp),
                    contentAlignment = Alignment.TopCenter
                ) {
                    Text(
                        text = "Поки що немає записів. Додайте перший!",
                        color = AppColors.muted,
                        textAlign = TextAlign.Center
                    )
                }
            } else {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 16.dp)
                ) {
                    items(entries, key = { it.id }) { entry ->
                        EntryCard(entry = entry)
                    }
                }
            }
        }

        PrimaryButton(
            label = "Додати запис",
            icon = Icons.Default.Add,
            modifier = Modifier.padding(bottom = 12.dp),
            onPressed = { showAddSheet = true }
        )
    }

    if (showAddSheet) {
        AddEntrySheet(
            viewModel = viewModel,
            onDismiss = { showAddSheet = false },
            onSavedWithError = { errorMsg ->
                showAddSheet = false
                if (errorMsg != null) {
                    scope.launch {
                        snackbarHostState.showSnackbar(
                            "Не вдалося синхронізувати ($errorMsg). Запис збережено локально."
                        )
                    }
                }
            }
        )
    }
}

@Composable
private fun EntryCard(entry: SleepEntry) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(AppRadius.md))
            .background(AppColors.card)
            .border(1.dp, AppColors.border, RoundedCornerShape(AppRadius.md))
            .padding(16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = "${entry.bedtime} – ${entry.wake}",
                color = AppColors.text,
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${formatDuration(entry.durationMin)} · ${entry.date}",
                color = AppColors.muted,
                fontSize = 12.sp
            )
            if (entry.notes.isNotEmpty()) {
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = entry.notes,
                    color = AppColors.muted,
                    fontSize = 13.sp,
                    fontStyle = FontStyle.Italic
                )
            }
        }

        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text(
                text = qualities.getOrElse(entry.quality) { qualities[2] }.emoji,
                fontSize = 24.sp
            )
            Spacer(modifier = Modifier.height(6.dp))
            Icon(
                imageVector = if (entry.synced) Icons.Default.CloudDone else Icons.Default.CloudOff,
                contentDescription = null,
                modifier = Modifier.size(16.dp),
                tint = if (entry.synced) AppColors.success else AppColors.muted
            )
        }
    }
}
