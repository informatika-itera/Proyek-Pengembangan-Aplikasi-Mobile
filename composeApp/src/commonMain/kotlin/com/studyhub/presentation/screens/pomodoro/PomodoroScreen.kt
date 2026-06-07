package com.studyhub.presentation.screens.pomodoro

import androidx.compose.runtime.Composable
import androidx.navigation.NavController

@Composable
expect fun PomodoroScreen(
    taskId: String? = null,
    navController: NavController
)
