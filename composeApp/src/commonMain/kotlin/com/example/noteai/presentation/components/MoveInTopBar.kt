package com.example.noteai.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.noteai.presentation.theme.MoveInTheme

@Composable
fun MoveInTopBar(
    onResetClick: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .padding(horizontal = 16.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "MoveIn.",
            style = MoveInTheme.typography.displayMedium.copy(
                fontSize = 20.sp,
                color = MoveInTheme.colors.textPrimary
            )
        )

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(20.dp))
                .background(MoveInTheme.colors.surfaceSecondary)
                .clickable { onResetClick() }
                .padding(horizontal = 12.dp, vertical = 6.dp)
        ) {
            Text(
                text = "Reset State",
                style = MoveInTheme.typography.labelSmall.copy(
                    color = MoveInTheme.colors.textSecondary
                )
            )
        }
    }
}
