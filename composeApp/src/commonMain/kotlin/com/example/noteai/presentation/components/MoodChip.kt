package com.example.noteai.presentation.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
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
            .height(166.dp)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(30.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.08f)
        ),
        border = BorderStroke(
            width = 1.dp,
            color = mood.accent.copy(alpha = 0.20f)
        )
    ) {
        Column(
            verticalArrangement = Arrangement.SpaceBetween,
            modifier = Modifier
                .fillMaxHeight()
                .padding(18.dp)
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(48.dp)
                    .clip(CircleShape)
                    .background(mood.accent.copy(alpha = 0.16f))
            ) {
                Text(
                    text = mood.emoji,
                    fontSize = 24.sp
                )
            }

            Column {
                Text(
                    text = mood.title,
                    color = Color.White,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.ExtraBold
                )

                androidx.compose.foundation.layout.Spacer(
                    modifier = Modifier.height(6.dp)
                )

                Text(
                    text = mood.subtitle,
                    color = Color.White.copy(alpha = 0.58f),
                    fontSize = 11.sp,
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
        color = accent.copy(alpha = 0.13f),
        shape = RoundedCornerShape(999.dp),
        border = BorderStroke(
            width = 1.dp,
            color = accent.copy(alpha = 0.25f)
        )
    ) {
        Text(
            text = text,
            color = accent,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp)
        )
    }
}