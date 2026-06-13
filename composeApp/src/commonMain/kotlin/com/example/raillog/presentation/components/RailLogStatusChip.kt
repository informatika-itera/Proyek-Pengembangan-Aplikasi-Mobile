package com.example.raillog.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raillog.presentation.theme.RailLogColors

@Composable
fun RailLogStatusChip(
    status: String
) {
    val bgColor = when (status.uppercase()) {
        "VERIFIED" -> RailLogColors.Success50
        "PENDING" -> RailLogColors.Warning50
        "REJECTED" -> RailLogColors.Danger50
        else -> RailLogColors.Neutral100
    }

    val textColor = when (status.uppercase()) {
        "VERIFIED" -> RailLogColors.Success600
        "PENDING" -> RailLogColors.Warning600
        "REJECTED" -> RailLogColors.Danger600
        else -> Color.Black
    }

    Box(
        modifier = Modifier
            .background(
                bgColor,
                RoundedCornerShape(8.dp)
            )
            .padding(
                horizontal = 10.dp,
                vertical = 4.dp
            )
    ) {
        Text(
            text = status,
            color = textColor,
            fontSize = 11.sp,
            fontWeight = FontWeight.ExtraBold
        )
    }
}