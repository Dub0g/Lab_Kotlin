package com.example.sleephelper.screens

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.sleephelper.state.SleepViewModel
import com.example.sleephelper.ui.theme.AppColors

private data class NavItem(val label: String, val icon: ImageVector)

private val navItems = listOf(
    NavItem("Головна", Icons.Default.Home),
    NavItem("Щоденник", Icons.Default.MenuBook),
    NavItem("Статистика", Icons.Default.BarChart),
    NavItem("Розрахунок", Icons.Default.AccessTimeFilled),
    NavItem("Релакс", Icons.Default.NightlightRound)
)

@Composable
fun AppShell(viewModel: SleepViewModel = viewModel()) {
    var currentIndex by rememberSaveable { mutableIntStateOf(0) }
    val snackbarHostState = remember { SnackbarHostState() }

    Scaffold(
        containerColor = AppColors.bg,
        snackbarHost = {
            SnackbarHost(hostState = snackbarHostState) { data ->
                Snackbar(
                    containerColor = AppColors.card,
                    contentColor = AppColors.danger,
                    snackbarData = data
                )
            }
        },
        bottomBar = {
            Column {
                HorizontalDivider(color = AppColors.border, thickness = 1.dp)
                NavigationBar(
                    containerColor = AppColors.bg,
                    tonalElevation = 0.dp
                ) {
                    navItems.forEachIndexed { index, item ->
                        NavigationBarItem(
                            selected = currentIndex == index,
                            onClick = { currentIndex = index },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label, fontSize = 10.sp) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = AppColors.accent,
                                selectedTextColor = AppColors.accent,
                                unselectedIconColor = AppColors.muted,
                                unselectedTextColor = AppColors.muted,
                                indicatorColor = AppColors.bg
                            )
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = innerPadding.calculateBottomPadding())
        ) {
            when (currentIndex) {
                0 -> HomeScreen(viewModel = viewModel, onNavigate = { currentIndex = it })
                1 -> DiaryScreen(viewModel = viewModel, snackbarHostState = snackbarHostState)
                2 -> PlaceholderScreen(title = "Статистика")
                3 -> CalcScreen()
                4 -> PlaceholderScreen(title = "Релакс")
            }
        }
    }
}
