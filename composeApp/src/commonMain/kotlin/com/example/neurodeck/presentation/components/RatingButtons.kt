package com.example.neurodeck.presentation.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.neurodeck.domain.model.ReviewRating

@Composable
fun RatingButtonRow(
    onRate: (ReviewRating) -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        RatingButton(
            rating = ReviewRating.AGAIN,
            label = "Lupa",
            color = Color(0xFFD32F2F),  // red 700
            onClick = { onRate(ReviewRating.AGAIN) },
            enabled = enabled,
            modifier = Modifier.weight(1f),
        )
        RatingButton(
            rating = ReviewRating.HARD,
            label = "Sulit",
            color = Color(0xFFF57C00),  // orange 700
            onClick = { onRate(ReviewRating.HARD) },
            enabled = enabled,
            modifier = Modifier.weight(1f),
        )
        RatingButton(
            rating = ReviewRating.GOOD,
            label = "Oke",
            color = MaterialTheme.colorScheme.primary,
            onClick = { onRate(ReviewRating.GOOD) },
            enabled = enabled,
            modifier = Modifier.weight(1f),
        )
        RatingButton(
            rating = ReviewRating.EASY,
            label = "Mudah",
            color = Color(0xFF388E3C),  // green 700
            onClick = { onRate(ReviewRating.EASY) },
            enabled = enabled,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun RatingButton(
    rating: ReviewRating,
    label: String,
    color: Color,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    Button(
        onClick = onClick,
        enabled = enabled,
        shape = RoundedCornerShape(12.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = color,
            contentColor = Color.White,
        ),
        modifier = modifier.height(56.dp),
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
    }
}
