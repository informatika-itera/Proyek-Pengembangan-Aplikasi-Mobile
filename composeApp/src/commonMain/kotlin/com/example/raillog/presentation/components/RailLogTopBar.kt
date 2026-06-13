package com.example.raillog.presentation.components

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.example.raillog.presentation.theme.RailLogColors

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RailLogTopBar(
    title: String
) {
    TopAppBar(
        title = {
            Text(
                text = title,
                fontWeight = FontWeight.ExtraBold,
                color = RailLogColors.PrimaryAction
            )
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.White
        )
    )
}