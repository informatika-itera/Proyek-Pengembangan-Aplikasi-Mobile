package com.example.raillog.presentation.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun KPIWidget(
    icon: ImageVector,
    title: String,
    value: String,
    accentColor: Color,
    modifier: Modifier = Modifier
) {

    EnterpriseCard(
        modifier = modifier
    ) {

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement =
                Arrangement.SpaceBetween
        ) {

            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(
                        accentColor.copy(alpha = 0.15f),
                        RoundedCornerShape(10.dp)
                    ),
                contentAlignment = Alignment.Center
            ) {

                Icon(
                    icon,
                    null,
                    tint = accentColor
                )
            }

            Text(
                text = value,
                fontSize = 28.sp,
                fontWeight = FontWeight.ExtraBold,
                color = accentColor
            )
        }

        Spacer(
            modifier = Modifier.height(12.dp)
        )

        Text(
            text = title,
            fontSize = 12.sp,
            fontWeight = FontWeight.Bold
        )
    }
}