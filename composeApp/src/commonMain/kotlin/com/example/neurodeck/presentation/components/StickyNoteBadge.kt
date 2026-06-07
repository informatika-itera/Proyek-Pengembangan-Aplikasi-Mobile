package com.example.neurodeck.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

/**
 * @param text             Teks yang ditampilkan.
 * @param modifier         Optional Modifier untuk size/position.
 * @param containerColor   Background. Default = primaryContainer (violet-tint lembut).
 * @param contentColor     Text color. Default = onPrimaryContainer.
 * @param borderColor      Border stroke color (kalau showBorder).
 * @param showBorder       Toggle border. Default FALSE (chip lembut tanpa border).
 */
@Composable
fun StickyNoteBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.primaryContainer,
    contentColor: Color = MaterialTheme.colorScheme.onPrimaryContainer,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    showBorder: Boolean = false,
) {
    val shape = RoundedCornerShape(8.dp)

    Box(
        modifier = modifier
            .background(color = containerColor, shape = shape)
            .then(
                if (showBorder) {
                    Modifier.border(
                        border = BorderStroke(1.dp, borderColor),
                        shape = shape,
                    )
                } else Modifier,
            )
            .padding(horizontal = 10.dp, vertical = 5.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = contentColor,
        )
    }
}
