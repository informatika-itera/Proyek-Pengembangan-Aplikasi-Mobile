package com.example.neurodeck.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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

// ════════════════════════════════════════════════════════════════════════════
// RatingButtons.kt — REFACTORED to Stitch style (Sprint 2 UI polish)
//
// Perubahan:
//   - Pakai pastel container colors (bukan solid bright red/orange/green)
//   - Bordered pill style: outlined dengan border tegas, fill pastel di dalam
//   - TAMBAH interval preview text di bawah label ("<1m", "6h", "10m", "4d")
//   - Default preview untuk Card baru (repetitions=0): "<1m" / "<10m" / "10m" / "4d"
//   - Caller bisa override via intervalPreviews map kalau punya real computation
//
// Layout button:
//   ┌──────────┐
//   │  Lupa    │  ← label uppercase
//   │  <1m     │  ← interval preview (smaller, opacity)
//   └──────────┘
// ════════════════════════════════════════════════════════════════════════════

/**
 * Row dengan 4 button rating untuk Study Session.
 *
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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
    ) {
        RatingPill(
            label = "Lupa",
            intervalPreview = intervalPreviews[ReviewRating.AGAIN].orEmpty(),
            containerColor = AgainContainer,
            contentColor = AgainOnContainer,
            borderColor = AgainBorder,
            onClick = { onRate(ReviewRating.AGAIN) },
            enabled = enabled,
            modifier = Modifier.weight(1f),
        )
        RatingPill(
            label = "Sulit",
            intervalPreview = intervalPreviews[ReviewRating.HARD].orEmpty(),
            containerColor = HardContainer,
            contentColor = HardOnContainer,
            borderColor = HardBorder,
            onClick = { onRate(ReviewRating.HARD) },
            enabled = enabled,
            modifier = Modifier.weight(1f),
        )
        RatingPill(
            label = "Oke",
            intervalPreview = intervalPreviews[ReviewRating.GOOD].orEmpty(),
            containerColor = GoodContainer,
            contentColor = GoodOnContainer,
            borderColor = GoodBorder,
            onClick = { onRate(ReviewRating.GOOD) },
            enabled = enabled,
            modifier = Modifier.weight(1f),
        )
        RatingPill(
            label = "Mudah",
            intervalPreview = intervalPreviews[ReviewRating.EASY].orEmpty(),
            containerColor = EasyContainer,
            contentColor = EasyOnContainer,
            borderColor = EasyBorder,
            onClick = { onRate(ReviewRating.EASY) },
            enabled = enabled,
            modifier = Modifier.weight(1f),
        )
    }
}

/**
 * Bordered pill rating button.
 *
 * Visual: pastel container + tegas border + label uppercase + interval kecil di bawah.
 * Pakai Box+clickable bukan Button supaya custom border + interior content fleksibel.
 */
@Composable
private fun RatingPill(
    label: String,
    intervalPreview: String,
    containerColor: Color,
    contentColor: Color,
    borderColor: Color,
    onClick: () -> Unit,
    enabled: Boolean,
    modifier: Modifier = Modifier,
) {
    val shape = RoundedCornerShape(12.dp)
    Box(
        modifier = modifier
            .height(72.dp)
            .clip(shape)
            .background(containerColor)
            .border(BorderStroke(1.5.dp, borderColor), shape)
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
                text = label.uppercase(),
                style = MaterialTheme.typography.labelMedium,
                fontWeight = FontWeight.Bold,
                color = contentColor,
                textAlign = TextAlign.Center,
            )
            if (intervalPreview.isNotEmpty()) {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = intervalPreview,
                    style = MaterialTheme.typography.labelSmall,
                    color = contentColor.copy(alpha = 0.7f),
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

// ════════════════════════════════════════════════════════════════════════════
// COLOR TOKENS — pastel containers + tegas borders
//
// Hardcoded warna karena ini semantic color (rating system universal — red/orange
// /green/purple konsisten across themes). Kalau mau adapt ke dark mode, bisa
// di-wrap dalam @Composable + isSystemInDarkTheme branch.
//
// Tone pastel = inspired by Stitch mockup "Again/Hard/Good/Easy" pills.
// ════════════════════════════════════════════════════════════════════════════

// AGAIN — soft red pastel
private val AgainContainer = Color(0xFFFEE2E2)  // red-100
private val AgainOnContainer = Color(0xFF991B1B)  // red-800
private val AgainBorder = Color(0xFFEF4444)  // red-500

// HARD — soft amber pastel
private val HardContainer = Color(0xFFFEF3C7)  // amber-100
private val HardOnContainer = Color(0xFF92400E)  // amber-800
private val HardBorder = Color(0xFFF59E0B)  // amber-500

// GOOD — soft green pastel
private val GoodContainer = Color(0xFFDCFCE7)  // green-100
private val GoodOnContainer = Color(0xFF14532D)  // green-900
private val GoodBorder = Color(0xFF22C55E)  // green-500

// EASY — soft purple pastel (signature NeuroDeck)
private val EasyContainer = Color(0xFFEDE9FE)  // violet-100
private val EasyOnContainer = Color(0xFF5B21B6)  // violet-800
private val EasyBorder = Color(0xFF8B5CF6)  // violet-500

// ════════════════════════════════════════════════════════════════════════════
// DEFAULT PREVIEWS for new cards (repetitions = 0)
//
// Sesuai SM-2 baseline behavior:
//   - AGAIN → reset, due lagi dalam 1 hari (tapi UI tampilkan "<1m" karena
//     biasanya user mau re-test immediately untuk learning)
//   - HARD → ~10 menit (re-test segera)
//   - GOOD → ~10 menit (re-test segera kalau new card, atau interval lama
//     kalau sudah punya history)
//   - EASY → 4 hari (skip ke interval lebih panjang)
//
// Untuk card yang sudah punya history (repetitions > 0), caller harus pass
// computed previews via intervalPreviews param.
// ════════════════════════════════════════════════════════════════════════════
val DEFAULT_NEW_CARD_PREVIEWS: Map<ReviewRating, String> = mapOf(
    ReviewRating.AGAIN to "<1m",
    ReviewRating.HARD to "<10m",
    ReviewRating.GOOD to "10m",
    ReviewRating.EASY to "4d",
)