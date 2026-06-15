package com.studyhub.presentation.navigation

import androidx.compose.runtime.Composable

@Composable
fun AppNavHost(
    openScreen: String? = null,
    taskId: String? = null,
    onScreenOpened: () -> Unit = {}
) {
    AppNavigation(
        openScreen = openScreen,
        taskId = taskId,
        onScreenOpened = onScreenOpened
    )
}
