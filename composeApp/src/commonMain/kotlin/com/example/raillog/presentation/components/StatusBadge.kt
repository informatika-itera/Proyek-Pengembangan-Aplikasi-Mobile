package com.example.raillog.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raillog.presentation.theme.RailLogColors

@Composable
fun StatusBadge(
    text: String
) {

    val bgColor =
        when (text.uppercase()) {

            "VERIFIED" ->
                RailLogColors.Success50

            "PENDING" ->
                RailLogColors.Warning50

            "REJECTED" ->
                RailLogColors.Danger50

            else ->
                RailLogColors.AISurface
        }

    val textColor =
        when (text.uppercase()) {

            "VERIFIED" ->
                RailLogColors.Success600

            "PENDING" ->
                RailLogColors.Warning600

            "REJECTED" ->
                RailLogColors.Danger600

            else ->
                RailLogColors.PrimaryAction
        }

    Text(
        text = text,
        color = textColor,
        fontWeight = FontWeight.ExtraBold,
        fontSize = 11.sp,
        modifier = Modifier
            .background(
                bgColor,
                RoundedCornerShape(8.dp)
            )
            .padding(
                horizontal = 10.dp,
                vertical = 5.dp
            )
    )
}