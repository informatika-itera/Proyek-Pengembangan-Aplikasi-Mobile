package com.example.noteai.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun MoveInStateMessage(
    title: String,
    message: String,
    type: StateMessageType = StateMessageType.EMPTY,
    actionText: String? = null,
    onActionClick: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val icon = when (type) {
        StateMessageType.EMPTY -> Icons.Default.Info
        StateMessageType.ERROR -> Icons.Default.ErrorOutline
    }

    val accentColor = when (type) {
        StateMessageType.EMPTY -> Color(0xFF3B82F6)
        StateMessageType.ERROR -> Color(0xFFEF4444)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = accentColor.copy(alpha = 0.08f),
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                color = accentColor.copy(alpha = 0.18f),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(20.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = accentColor,
            modifier = Modifier.size(32.dp)
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = title,
            fontSize = 16.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = message,
            fontSize = 12.sp,
            lineHeight = 18.sp,
            color = Color.White.copy(alpha = 0.72f),
            textAlign = TextAlign.Center
        )

        if (actionText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(12.dp))

            TextButton(
                onClick = onActionClick
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(14.dp)
                )

                Spacer(modifier = Modifier.width(6.dp))

                Text(
                    text = actionText,
                    color = accentColor,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

enum class StateMessageType {
    EMPTY,
    ERROR
}