package com.studyhub.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import com.studyhub.core.util.headerBrownColor
import com.studyhub.presentation.theme.Spacing

@Composable
fun ScreenHeader(
    modifier: Modifier = Modifier,
    content: @Composable BoxScope.() -> Unit
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(
                RoundedCornerShape(
                    bottomStart = 24.dp,
                    bottomEnd = 24.dp
                )
            )
            .background(headerBrownColor())
            .statusBarsPadding()
            .padding(
                start = Spacing.normal,
                end = Spacing.normal,
                top = Spacing.normal,
                bottom = Spacing.extraLarge
            ),
        content = content
    )
}
