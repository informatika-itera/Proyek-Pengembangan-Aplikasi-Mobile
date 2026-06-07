package com.kelazzz.app.presentation.components

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

@Composable
expect fun ScheduleDateField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier
)

@Composable
expect fun ScheduleTimeField(
    value: String,
    onValueChange: (String) -> Unit,
    modifier: Modifier = Modifier
)
