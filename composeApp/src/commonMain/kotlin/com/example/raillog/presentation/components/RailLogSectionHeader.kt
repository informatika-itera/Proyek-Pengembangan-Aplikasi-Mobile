package com.example.raillog.presentation.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.raillog.presentation.theme.RailLogColors

@Composable
fun RailLogSectionHeader(
    title: String,
    subtitle: String? = null
) {
    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = title,
            fontSize = 22.sp,
            fontWeight = FontWeight.ExtraBold,
            color = RailLogColors.PrimaryAction
        )

        subtitle?.let {
            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = it,
                fontSize = 13.sp
            )
        }
    }
}