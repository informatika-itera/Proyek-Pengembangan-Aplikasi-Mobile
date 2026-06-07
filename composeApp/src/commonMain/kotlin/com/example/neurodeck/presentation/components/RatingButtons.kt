package com.example.neurodeck.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.neurodeck.domain.model.ReviewRating

/**
 * @param onRate              Callback saat user pilih rating.
 * @param intervalPreviews    Map rating → interval label (e.g., "<1m", "6h", "4d").
 *                            Optional — kalau null/missing, pakai default preview
 *                            untuk Card baru.
 * @param enabled             Disable saat sedang processing transition.
 */
@Composable
fun RatingButtonRow(
    onRate: (ReviewRating) -> Unit,
    modifier: Modifier = Modifier,
    intervalPreviews: Map<ReviewRating, String> = DEFAULT_NEW_CARD_PREVIEWS,
    enabled: Boolean = true,
) {
    val colors = rememberRatingColors()
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        RatingPill(
            label = "Lupa",
            intervalPreview = intervalPreviews[ReviewRating.AGAIN].orEmpty(),
            colors = colors.getValue(ReviewRating.AGAIN),
            onClick = { onRate(ReviewRating.AGAIN) },
            enabled = enabled,
            modifier = Modifier.weight(1f),
        )
        RatingPill(
            label = "Sulit",
            intervalPreview = intervalPreviews[ReviewRating.HARD].orEmpty(),
            colors = colors.getValue(ReviewRating.HARD),
            onClick = { onRate(ReviewRating.HARD) },
            enabled = enabled,
            modifier = Modifier.weight(1f),
        )
        RatingPill(
            label = "Oke",
            intervalPreview = intervalPreviews[ReviewRating.GOOD].orEmpty(),
            colors = colors.getValue(ReviewRating.GOOD),
            onClick = { onRate(ReviewRating.GOOD) },
            enabled = enabled,
            modifier = Modifier.weight(1f),
        )
        RatingPill(
            label = "Mudah",
            intervalPreview = intervalPreviews[ReviewRating.EASY].orEmpty(),
            colors = colors.getValue(ReviewRating.EASY),
            onClick = { onRate(ReviewRating.EASY) },
            enabled = enabled,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun RatingPill(
    label: String,
    intervalPreview: String,
    colors: RatingColors,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(14.dp)
    Box(
        modifier = modifier
            .height(72.dp)
            .clip(shape)
            .background(colors.container)
            .border(BorderStroke(1.dp, colors.border), shape)
            .clickable(enabled = enabled, onClick = onClick)
            .alpha(if (enabled) 1f else 0.5f)
            .padding(horizontal = 8.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center,
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelLarge,
                fontWeight = FontWeight.Bold,
                color = colors.onContainer,
                textAlign = TextAlign.Center,
            )
            if (intervalPreview.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = intervalPreview,
                    style = MaterialTheme.typography.labelSmall,
                    color = colors.onContainer.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

// COLOR TOKENS
private data class RatingColors(
    val container: Color,
    val onContainer: Color,
    val border: Color,
)

@Composable
private fun rememberRatingColors(): Map<ReviewRating, RatingColors> {
    return if (isSystemInDarkTheme()) {
        mapOf(
            ReviewRating.AGAIN to RatingColors(Color(0xFF3A1A1D), Color(0xFFFCA5A5), Color(0xFFEF4444)),
            ReviewRating.HARD to RatingColors(Color(0xFF2E2410), Color(0xFFFDE68A), Color(0xFFF59E0B)),
            ReviewRating.GOOD to RatingColors(Color(0xFF122A1B), Color(0xFF86EFAC), Color(0xFF22C55E)),
            ReviewRating.EASY to RatingColors(Color(0xFF2E2747), Color(0xFFC4B5FD), Color(0xFF8B5CF6)),
        )
    } else {
        mapOf(
            ReviewRating.AGAIN to RatingColors(Color(0xFFFEE2E2), Color(0xFF991B1B), Color(0xFFEF4444)),
            ReviewRating.HARD to RatingColors(Color(0xFFFEF3C7), Color(0xFF92400E), Color(0xFFF59E0B)),
            ReviewRating.GOOD to RatingColors(Color(0xFFDCFCE7), Color(0xFF14532D), Color(0xFF22C55E)),
            ReviewRating.EASY to RatingColors(Color(0xFFEDE9FE), Color(0xFF5B21B6), Color(0xFF8B5CF6)),
        )
    }
}

// DEFAULT PREVIEWS for new cards (repetitions = 0)
val DEFAULT_NEW_CARD_PREVIEWS: Map<ReviewRating, String> = mapOf(
    ReviewRating.AGAIN to "<1m",
    ReviewRating.HARD to "<10m",
    ReviewRating.GOOD to "10m",
    ReviewRating.EASY to "4d",
)
