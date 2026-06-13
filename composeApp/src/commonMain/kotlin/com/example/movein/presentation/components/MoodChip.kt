package com.example.movein.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.movein.presentation.theme.MoveInTheme

data class MoveInMood(
    val id: String,
    val title: String,
    val subtitle: String,
    val emoji: String,
    val accent: Color,
    val background: Color
)

val moveInMoods = listOf(
    MoveInMood(
        id = "bored",
        title = "Bosan",
        subtitle = "Butuh hal kecil yang bikin hidup terasa gerak lagi.",
        emoji = "😶",
        accent = Color(0xFF22D3EE),
        background = Color(0xFF051014)
    ),
    MoveInMood(
        id = "sad",
        title = "Sedih",
        subtitle = "Pelan-pelan aja. Hari ini tidak harus kuat terus.",
        emoji = "🌧️",
        accent = Color(0xFF60A5FA),
        background = Color(0xFF020617)
    ),
    MoveInMood(
        id = "tired",
        title = "Capek",
        subtitle = "Energi sedang tipis, pilih aktivitas yang ringan dulu.",
        emoji = "🫠",
        accent = Color(0xFFF59E0B),
        background = Color(0xFF120A02)
    ),
    MoveInMood(
        id = "excited",
        title = "Semangat",
        subtitle = "Gas tipis-tipis. Arahkan energimu ke hal produktif.",
        emoji = "⚡",
        accent = Color(0xFFA3E635),
        background = Color(0xFF071006)
    ),
    MoveInMood(
        id = "gabut",
        title = "Gabut",
        subtitle = "Waktunya cari aktivitas random yang tetap meaningful.",
        emoji = "🎮",
        accent = Color(0xFFC084FC),
        background = Color(0xFF10071A)
    ),
    MoveInMood(
        id = "stress",
        title = "Stress",
        subtitle = "Tenangkan sistem dulu. Tidak semua harus selesai sekarang.",
        emoji = "🔥",
        accent = Color(0xFFFB7185),
        background = Color(0xFF170508)
    )
)

@Composable
fun MoodCard(
    mood: MoveInMood,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier
            .height(180.dp)
            .clickable(onClick = onClick),
        shape = MoveInTheme.shapes.large,
        colors = CardDefaults.cardColors(
            containerColor = MoveInTheme.colors.cardPrimary
        ),
        border = BorderStroke(
            width = 1.dp,
            color = mood.accent.copy(alpha = 0.3f)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxHeight()
                .padding(20.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(52.dp)
                    .clip(CircleShape)
                    .background(mood.accent.copy(alpha = 0.15f))
            ) {
                Text(
                    text = mood.emoji,
                    fontSize = 26.sp
                )
            }

            Column {
                Text(
                    text = mood.title,
                    style = MoveInTheme.typography.titleLarge.copy(color = MoveInTheme.colors.textPrimary)
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = mood.subtitle,
                    style = MoveInTheme.typography.labelSmall.copy(color = MoveInTheme.colors.textMuted),
                    lineHeight = 15.sp
                )
            }
        }
    }
}

@Composable
fun InfoPill(
    text: String,
    accent: Color
) {
    Surface(
        color = accent.copy(alpha = 0.15f),
        shape = CircleShape,
        border = BorderStroke(
            width = 1.dp,
            color = accent.copy(alpha = 0.3f)
        )
    ) {
        Text(
            text = text,
            color = accent,
            style = MoveInTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
            modifier = Modifier.padding(horizontal = 14.dp, vertical = 8.dp)
        )
    }
}
