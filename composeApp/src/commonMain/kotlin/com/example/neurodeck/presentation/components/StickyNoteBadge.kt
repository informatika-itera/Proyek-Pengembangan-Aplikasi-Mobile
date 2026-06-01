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

// ════════════════════════════════════════════════════════════════════════════
// StickyNoteBadge.kt
//
// Signature visual element dari design Vivid Logic — small label dengan:
//   - Background warna terang (yellow/orange di light mode, atau theme tertiary)
//   - Border tegas (black/outline color)
//   - Rounded corner kecil
//   - Text font weight semibold
//
// Pakai untuk:
//   - Streak banner (15 Day Streak)
//   - Progress completion (65% Complete)
//   - Status pill (New, Critical, Mastered)
//   - Notification count
//
// Contoh:
//   StickyNoteBadge(text = "15 Day Streak")
//   StickyNoteBadge(text = "NEW!", containerColor = MaterialTheme.colorScheme.error)
// ════════════════════════════════════════════════════════════════════════════

/**
 * Sticky note style badge — eye-catching label kecil.
 *
 * @param text             Teks yang ditampilkan.
 * @param modifier         Optional Modifier untuk size/position.
 * @param containerColor   Background color. Default = tertiary (yellow/orange).
 * @param contentColor     Text color. Default = onTertiary (black/dark).
 * @param borderColor      Border stroke color. Default = outline (black/dark).
 * @param showBorder       Toggle border visibility. Default true sesuai design.
 */
@Composable
fun StickyNoteBadge(
    text: String,
    modifier: Modifier = Modifier,
    containerColor: Color = MaterialTheme.colorScheme.tertiary,
    contentColor: Color = MaterialTheme.colorScheme.onTertiary,
    borderColor: Color = MaterialTheme.colorScheme.outline,
    showBorder: Boolean = true,
) {
    val shape = RoundedCornerShape(6.dp)

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
            .padding(horizontal = 10.dp, vertical = 4.dp),
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            fontWeight = FontWeight.SemiBold,
            color = contentColor,
        )
    }
}