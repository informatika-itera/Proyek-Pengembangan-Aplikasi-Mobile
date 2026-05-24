package com.studyhub.presentation.screens.profile

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.NavController
import com.studyhub.presentation.components.*
import com.studyhub.presentation.theme.Spacing
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun ProfileScreen(navController: NavController) {
    val viewModel: ProfileViewModel = koinViewModel()
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(bottom = Spacing.large)
    ) {
        // ── Header ──
        item {
            ProfileHeader(userName = uiState.userName)
        }

        // ── Stats ──
        item {
            ProfileStatsRow(
                overdueCount = uiState.overdueCount
            )
        }

        // ── Settings Section ──
        item {
            Text(
                "Pengaturan",
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.padding(
                    horizontal = Spacing.normal,
                    vertical = Spacing.small
                ),
                color = MaterialTheme.colorScheme.primary
            )
        }

        // Dark Mode toggle — utama
        item {
            DarkModeToggleItem(
                isDarkMode = uiState.isDarkMode,
                onToggle = { viewModel.toggleDarkMode() }
            )
        }

        item { HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.normal)) }

        // Notification toggle
        item {
            SettingsSwitchItem(
                title = "Notifikasi",
                subtitle = "Pengingat deadline tugas",
                icon = Icons.Default.Notifications,
                checked = uiState.notificationEnabled,
                onCheckedChange = { /* TODO Sprint 3 */ }
            )
        }

        item { HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.normal)) }

        // AI Reminder toggle
        item {
            SettingsSwitchItem(
                title = "AI Reminder",
                subtitle = "Jadwal pengingat adaptif",
                icon = Icons.Default.AutoAwesome,
                checked = uiState.isAiReminderEnabled,
                onCheckedChange = { /* TODO Sprint 3 */ }
            )
        }

        item { HorizontalDivider(modifier = Modifier.padding(horizontal = Spacing.normal)) }

        // Pomodoro settings
        item {
            PomodoroSettingsItem(
                focusDuration = uiState.pomodoroFocusDuration,
                shortBreak = uiState.pomodoroShortBreak,
                longBreak = uiState.pomodoroLongBreak,
                onNavigate = { /* TODO */ }
            )
        }
    }
}
